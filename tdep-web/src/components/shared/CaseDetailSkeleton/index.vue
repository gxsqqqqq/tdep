<template>
  <div class="case-skeleton">
    <div class="case-skeleton__hero">
      <div class="skeleton-box skeleton-box--btn" style="width:80px;height:32px" />
      <div class="skeleton-box skeleton-box--title" style="width:60%;height:28px;margin-top:16px" />
      <div class="skeleton-box" style="width:40%;height:20px;margin-top:12px" />
      <div class="skeleton-box" style="width:100%;height:1px;margin-top:20px" />
      <div class="skeleton-meta">
        <div class="skeleton-box" style="width:120px;height:16px" />
        <div class="skeleton-box" style="width:120px;height:16px" />
        <div class="skeleton-box" style="width:120px;height:16px" />
      </div>
    </div>
    <div class="skeleton-tabs">
      <div v-for="i in 5" :key="i" class="skeleton-box skeleton-tab-item" style="width:100px;height:44px" />
    </div>
    <div class="skeleton-layout">
      <div class="skeleton-main">
        <div class="skeleton-card">
          <div class="skeleton-box" style="width:30%;height:22px" />
          <div class="skeleton-timeline">
            <div v-for="i in 4" :key="i" class="skeleton-timeline-item">
              <div class="skeleton-box skeleton-circle" style="width:14px;height:14px;flex-shrink:0" />
              <div style="flex:1;display:flex;flex-direction:column;gap:8px">
                <div class="skeleton-box" style="width:45%;height:14px" />
                <div class="skeleton-box" style="width:70%;height:13px" />
                <div class="skeleton-box" style="width:50%;height:12px" />
              </div>
            </div>
          </div>
        </div>
        <div class="skeleton-card" style="margin-top:20px">
          <div class="skeleton-box" style="width:25%;height:22px" />
          <div class="skeleton-doc-list">
            <div v-for="i in 2" :key="i" class="skeleton-doc-item">
              <div class="skeleton-box skeleton-circle" style="width:24px;height:24px" />
              <div style="flex:1;display:flex;flex-direction:column;gap:6px">
                <div class="skeleton-box" style="width:60%;height:14px" />
                <div class="skeleton-box" style="width:35%;height:12px" />
              </div>
            </div>
          </div>
        </div>
      </div>
      <aside class="skeleton-sidebar">
        <div class="skeleton-panel">
          <div class="skeleton-box" style="width:60px;height:18px" />
          <div class="skeleton-actions">
            <div v-for="i in 5" :key="i" class="skeleton-box" style="width:100%;height:46px" />
          </div>
        </div>
        <div class="skeleton-panel">
          <div class="skeleton-box" style="width:72px;height:18px" />
          <div class="skeleton-people">
            <div v-for="i in 4" :key="i" class="skeleton-person">
              <div class="skeleton-box skeleton-circle" style="width:24px;height:24px" />
              <div style="flex:1;display:flex;flex-direction:column;gap:4px">
                <div class="skeleton-box" style="width:50px;height:11px" />
                <div class="skeleton-box" style="width:70px;height:14px" />
              </div>
            </div>
          </div>
        </div>
      </aside>
    </div>
  </div>
</template>

<script setup lang="ts">
</script>

<style scoped>
.case-skeleton {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-card);
}

.skeleton-box {
  border-radius: 6px;
  background: linear-gradient(90deg, #f0f0f0 25%, #e8e8e8 50%, #f0f0f0 75%);
  background-size: 200% 100%;
  animation: shimmer 1.5s infinite;
}

.skeleton-circle { border-radius: 50%; }

@keyframes shimmer {
  0% { background-position: -200% 0; }
  100% { background-position: 200% 0; }
}

.case-skeleton__hero {
  padding: 24px 28px;
  border-radius: var(--border-radius);
  background: linear-gradient(135deg, #a0c4ff 0%, #7b9fe8 100%);
  min-height: 180px;
}

.skeleton-meta {
  display: flex;
  gap: 24px;
  margin-top: 16px;
}

.skeleton-tabs {
  display: flex;
  gap: 8px;
  overflow: hidden;
}

.skeleton-layout {
  display: grid;
  grid-template-columns: 1fr 300px;
  gap: var(--spacing-card);
}

.skeleton-main {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-card);
}

.skeleton-card {
  background: white;
  border: 1px solid var(--border-color);
  border-radius: var(--border-radius);
  padding: var(--spacing-card);
}

.skeleton-timeline {
  margin-top: 20px;
  display: flex;
  flex-direction: column;
  gap: 24px;
  padding-left: 20px;
  border-left: 2px solid #f0f0f0;
}

.skeleton-timeline-item {
  display: flex;
  gap: 12px;
  position: relative;
  left: -9px;
}

.skeleton-doc-list {
  margin-top: 16px;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.skeleton-doc-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  border-radius: var(--border-radius-sm);
  background: #fafafa;
}

.skeleton-sidebar {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-card);
}

.skeleton-panel {
  background: white;
  border: 1px solid var(--border-color);
  border-radius: var(--border-radius);
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.skeleton-actions {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.skeleton-people {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.skeleton-person {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 6px 0;
  border-bottom: 1px solid #f5f5f5;
}

@media (max-width: 1024px) {
  .skeleton-layout {
    grid-template-columns: 1fr;
  }
  .skeleton-sidebar {
    flex-direction: row;
    flex-wrap: wrap;
  }
  .skeleton-panel {
    flex: 1;
    min-width: 260px;
  }
}

@media (max-width: 640px) {
  .skeleton-sidebar {
    flex-direction: column;
  }
  .skeleton-panel {
    min-width: unset;
  }
}
</style>
