import { ref, computed } from 'vue'
import { useEvidenceStore } from '@/lib/stores/evidence'

export function useEvidence() {
  const evidenceStore = useEvidenceStore()
  const loading = ref(false)
  const uploading = ref(false)
  const error = ref<string | null>(null)

  const evidences = computed(() => evidenceStore.evidences)
  const currentEvidence = computed(() => evidenceStore.currentEvidence)

  async function fetchEvidences(caseId: string) {
    loading.value = true
    error.value = null
    try {
      await evidenceStore.fetchEvidences(caseId)
    } catch (e) {
      error.value = e instanceof Error ? e.message : '获取证据列表失败'
      throw e
    } finally {
      loading.value = false
    }
  }

  async function uploadEvidence(caseId: string, file: File) {
    uploading.value = true
    error.value = null
    try {
      const evidence = await evidenceStore.uploadEvidence(caseId, file)
      return evidence
    } catch (e) {
      error.value = e instanceof Error ? e.message : '上传证据失败'
      throw e
    } finally {
      uploading.value = false
    }
  }

  async function deleteEvidence(evidenceId: string) {
    loading.value = true
    error.value = null
    try {
      await evidenceStore.deleteEvidence(evidenceId)
    } catch (e) {
      error.value = e instanceof Error ? e.message : '删除证据失败'
      throw e
    } finally {
      loading.value = false
    }
  }

  return {
    evidences: evidences as any,
    currentEvidence: currentEvidence as any,
    loading,
    uploading,
    error,
    fetchEvidences,
    uploadEvidence,
    deleteEvidence
  }
}
