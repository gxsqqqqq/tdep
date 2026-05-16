package com.tdep.evidence.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tdep.common.enums.ResultCode;
import com.tdep.common.exception.BusinessException;
import com.tdep.common.security.TdepUserPrincipal;
import com.tdep.evidence.audit.EvidenceAuditRecorder;
import com.tdep.evidence.dto.EvidenceFreezeRequest;
import com.tdep.evidence.dto.EvidencePageRequest;
import com.tdep.evidence.dto.EvidenceSealRequest;
import com.tdep.evidence.dto.EvidenceUploadRequest;
import com.tdep.evidence.dto.EvidenceVerifyRequest;
import com.tdep.evidence.entity.EvidenceChain;
import com.tdep.evidence.entity.EvidenceFile;
import com.tdep.evidence.entity.EvidenceHash;
import com.tdep.evidence.entity.EvidenceOperationLog;
import com.tdep.evidence.enums.EvidenceChainEventType;
import com.tdep.evidence.enums.EvidenceOperationType;
import com.tdep.evidence.enums.EvidenceStatus;
import com.tdep.evidence.enums.OperationResult;
import com.tdep.evidence.hash.HashResult;
import com.tdep.evidence.hash.HashService;
import com.tdep.evidence.mapper.EvidenceFileMapper;
import com.tdep.evidence.mapper.EvidenceHashMapper;
import com.tdep.evidence.mapper.EvidenceOperationLogMapper;
import com.tdep.evidence.seal.EvidencePdfSealService;
import com.tdep.evidence.security.EvidencePermissionChecker;
import com.tdep.evidence.service.EvidenceChainService;
import com.tdep.evidence.service.EvidenceService;
import com.tdep.evidence.storage.ObjectStorageService;
import com.tdep.evidence.storage.StorageObject;
import com.tdep.evidence.storage.StorageProperties;
import com.tdep.evidence.vo.EvidenceChainVO;
import com.tdep.evidence.vo.EvidenceDownloadUrlVO;
import com.tdep.evidence.vo.EvidenceHashVO;
import com.tdep.evidence.vo.EvidenceOperationLogVO;
import com.tdep.evidence.vo.EvidenceVO;
import com.tdep.evidence.vo.EvidenceVerifyVO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 电子证据业务服务实现。
 */
@Service
@RequiredArgsConstructor
public class EvidenceServiceImpl implements EvidenceService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.BASIC_ISO_DATE;

    private final EvidenceFileMapper evidenceFileMapper;

    private final EvidenceHashMapper evidenceHashMapper;

    private final EvidenceOperationLogMapper operationLogMapper;

    private final HashService hashService;

    private final ObjectStorageService objectStorageService;

    private final StorageProperties storageProperties;

    private final EvidenceChainService evidenceChainService;

    private final EvidenceAuditRecorder auditRecorder;

    private final EvidencePermissionChecker permissionChecker;

    private final EvidencePdfSealService pdfSealService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public EvidenceVO upload(MultipartFile file, EvidenceUploadRequest request, HttpServletRequest servletRequest) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "上传文件不能为空");
        }
        TdepUserPrincipal principal = permissionChecker.currentPrincipal();
        try {
            byte[] bytes = file.getBytes();
            HashResult hashResult = hashService.sha256(new BufferedInputStream(new ByteArrayInputStream(bytes)));
            String evidenceNo = generateEvidenceNo();
            String fileExt = resolveFileExt(file.getOriginalFilename());
            String objectKey = buildOriginalObjectKey(request.getCaseId(), evidenceNo, hashResult.getHashValue(), fileExt);
            StorageObject storageObject = objectStorageService.putObject(
                    storageProperties.getOriginalBucket(),
                    objectKey,
                    new ByteArrayInputStream(bytes),
                    hashResult.getFileSize(),
                    file.getContentType()
            );

            EvidenceFile evidenceFile = new EvidenceFile();
            evidenceFile.setEvidenceNo(evidenceNo);
            evidenceFile.setCaseId(request.getCaseId());
            evidenceFile.setUploaderId(principal.getUserId());
            evidenceFile.setFileName(file.getOriginalFilename());
            evidenceFile.setFileExt(fileExt);
            evidenceFile.setContentType(file.getContentType());
            evidenceFile.setFileSize(hashResult.getFileSize());
            evidenceFile.setBucketName(storageObject.getBucketName());
            evidenceFile.setObjectKey(storageObject.getObjectKey());
            evidenceFile.setStatus(EvidenceStatus.TIMESTAMPED.name());
            evidenceFile.setDescription(request.getDescription());
            evidenceFile.setUploadedAt(LocalDateTime.now());
            evidenceFileMapper.insert(evidenceFile);

            EvidenceHash evidenceHash = new EvidenceHash();
            evidenceHash.setEvidenceId(evidenceFile.getId());
            evidenceHash.setHashAlgorithm(hashResult.getAlgorithm());
            evidenceHash.setHashValue(hashResult.getHashValue());
            evidenceHash.setFileFingerprint(hashResult.getHashValue());
            evidenceHash.setFileSize(hashResult.getFileSize());
            evidenceHash.setCalculatedAt(LocalDateTime.now());
            evidenceHashMapper.insert(evidenceHash);

            evidenceChainService.append(evidenceFile.getId(), EvidenceChainEventType.UPLOAD, "证据文件上传");
            evidenceChainService.append(evidenceFile.getId(), EvidenceChainEventType.HASH_CALCULATED, "SHA-256：" + hashResult.getHashValue());
            evidenceChainService.append(evidenceFile.getId(), EvidenceChainEventType.STORED, "MinIO：" + storageObject.getBucketName() + "/" + storageObject.getObjectKey());
            evidenceChainService.append(evidenceFile.getId(), EvidenceChainEventType.TIMESTAMPED, "生成可信时间戳");
            auditRecorder.record(evidenceFile.getId(), EvidenceOperationType.UPLOAD, OperationResult.SUCCESS, "上传证据成功", servletRequest);
            return toVO(evidenceFile, evidenceHash);
        } catch (IOException exception) {
            auditRecorder.record(null, EvidenceOperationType.UPLOAD, OperationResult.FAILED, exception.getMessage(), servletRequest);
            throw new BusinessException(ResultCode.INTERNAL_ERROR, "读取上传文件失败");
        }
    }

    @Override
    public Page<EvidenceVO> page(EvidencePageRequest request) {
        Page<EvidenceFile> page = evidenceFileMapper.selectPage(Page.of(request.getPageNo(), request.getPageSize()),
                new LambdaQueryWrapper<EvidenceFile>()
                        .eq(request.getCaseId() != null, EvidenceFile::getCaseId, request.getCaseId())
                        .eq(StringUtils.hasText(request.getStatus()), EvidenceFile::getStatus, request.getStatus())
                        .like(StringUtils.hasText(request.getKeyword()), EvidenceFile::getFileName, request.getKeyword())
                        .orderByDesc(EvidenceFile::getUploadedAt));
        Page<EvidenceVO> result = Page.of(page.getCurrent(), page.getSize(), page.getTotal());
        result.setRecords(page.getRecords().stream()
                .map(file -> toVO(file, findHash(file.getId())))
                .toList());
        return result;
    }

    @Override
    public EvidenceVO detail(Long id, HttpServletRequest servletRequest) {
        EvidenceFile file = requireEvidence(id);
        auditRecorder.record(id, EvidenceOperationType.READ, OperationResult.SUCCESS, "查询证据详情", servletRequest);
        return toVO(file, findHash(id));
    }

    @Override
    public EvidenceHashVO hash(Long id, HttpServletRequest servletRequest) {
        requireEvidence(id);
        EvidenceHash hash = requireHash(id);
        auditRecorder.record(id, EvidenceOperationType.HASH_READ, OperationResult.SUCCESS, "查询证据Hash", servletRequest);
        return toHashVO(hash);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public EvidenceVerifyVO verify(Long id, EvidenceVerifyRequest request, HttpServletRequest servletRequest) {
        EvidenceFile file = requireEvidence(id);
        EvidenceHash originalHash = requireHash(id);
        try (InputStream inputStream = objectStorageService.getObject(file.getBucketName(), file.getObjectKey())) {
            HashResult current = hashService.sha256(inputStream);
            boolean matched = originalHash.getHashValue().equals(current.getHashValue());
            LocalDateTime now = LocalDateTime.now();
            originalHash.setVerifiedAt(now);
            originalHash.setVerifyResult(matched ? 1 : 0);
            evidenceHashMapper.updateById(originalHash);
            file.setStatus(matched ? EvidenceStatus.VERIFIED.name() : EvidenceStatus.VERIFY_FAILED.name());
            evidenceFileMapper.updateById(file);
            evidenceChainService.append(id, EvidenceChainEventType.VERIFIED,
                    "校验结果：" + matched + "，" + (request == null ? "" : request.getRemark()));
            auditRecorder.record(id, EvidenceOperationType.VERIFY, OperationResult.SUCCESS, "证据完整性校验：" + matched, servletRequest);
            return EvidenceVerifyVO.builder()
                    .evidenceId(id)
                    .algorithm(originalHash.getHashAlgorithm())
                    .originalHash(originalHash.getHashValue())
                    .currentHash(current.getHashValue())
                    .matched(matched)
                    .status(file.getStatus())
                    .verifyTime(now)
                    .build();
        } catch (IOException exception) {
            auditRecorder.record(id, EvidenceOperationType.VERIFY, OperationResult.FAILED, exception.getMessage(), servletRequest);
            throw new BusinessException(ResultCode.INTERNAL_ERROR, "证据校验失败");
        }
    }

    @Override
    public EvidenceDownloadUrlVO downloadUrl(Long id, HttpServletRequest servletRequest) {
        EvidenceFile file = requireEvidence(id);
        String url = objectStorageService.presignedGetUrl(file.getBucketName(), file.getObjectKey());
        evidenceChainService.append(id, EvidenceChainEventType.DOWNLOADED, "生成原始证据预签名下载地址");
        auditRecorder.record(id, EvidenceOperationType.DOWNLOAD, OperationResult.SUCCESS, "生成原始证据下载地址", servletRequest);
        return EvidenceDownloadUrlVO.builder()
                .evidenceId(id)
                .url(url)
                .expireAt(LocalDateTime.now().plusSeconds(storageProperties.getPresignedUrlSeconds()))
                .build();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public EvidenceVO seal(Long id, EvidenceSealRequest request, HttpServletRequest servletRequest) {
        EvidenceFile file = requireEvidence(id);
        EvidenceHash hash = requireHash(id);
        if (EvidenceStatus.FROZEN.name().equals(file.getStatus()) || EvidenceStatus.DELETED.name().equals(file.getStatus())) {
            throw new BusinessException(ResultCode.CONFLICT, "当前证据状态不允许固化");
        }
        try (InputStream pdf = pdfSealService.buildCertificate(file, hash)) {
            byte[] bytes = pdf.readAllBytes();
            String objectKey = buildSealObjectKey(file.getCaseId(), file.getId());
            objectStorageService.putObject(storageProperties.getSealedBucket(),
                    objectKey,
                    new ByteArrayInputStream(bytes),
                    bytes.length,
                    "application/pdf");
            file.setSealedBucketName(storageProperties.getSealedBucket());
            file.setSealedObjectKey(objectKey);
            file.setSealedAt(LocalDateTime.now());
            file.setStatus(EvidenceStatus.SEALED.name());
            evidenceFileMapper.updateById(file);
            evidenceChainService.append(id, EvidenceChainEventType.SEALED,
                    request == null ? "生成PDF固化凭证" : request.getRemark());
            auditRecorder.record(id, EvidenceOperationType.SEAL, OperationResult.SUCCESS, "PDF固化成功", servletRequest);
            return toVO(file, hash);
        } catch (IOException exception) {
            auditRecorder.record(id, EvidenceOperationType.SEAL, OperationResult.FAILED, exception.getMessage(), servletRequest);
            throw new BusinessException(ResultCode.INTERNAL_ERROR, "PDF固化失败");
        }
    }

    @Override
    public EvidenceDownloadUrlVO sealDownloadUrl(Long id, HttpServletRequest servletRequest) {
        EvidenceFile file = requireEvidence(id);
        if (!StringUtils.hasText(file.getSealedObjectKey())) {
            throw new BusinessException(ResultCode.CONFLICT, "证据尚未生成固化凭证");
        }
        String url = objectStorageService.presignedGetUrl(file.getSealedBucketName(), file.getSealedObjectKey());
        auditRecorder.record(id, EvidenceOperationType.DOWNLOAD, OperationResult.SUCCESS, "生成固化凭证下载地址", servletRequest);
        return EvidenceDownloadUrlVO.builder()
                .evidenceId(id)
                .url(url)
                .expireAt(LocalDateTime.now().plusSeconds(storageProperties.getPresignedUrlSeconds()))
                .build();
    }

    @Override
    public List<EvidenceChainVO> chains(Long id, HttpServletRequest servletRequest) {
        requireEvidence(id);
        auditRecorder.record(id, EvidenceOperationType.CHAIN_READ, OperationResult.SUCCESS, "查询证据链", servletRequest);
        return evidenceChainService.listByEvidenceId(id).stream().map(this::toChainVO).toList();
    }

    @Override
    public Boolean verifyChain(Long id, HttpServletRequest servletRequest) {
        requireEvidence(id);
        boolean verified = evidenceChainService.verifyChain(id);
        auditRecorder.record(id, EvidenceOperationType.CHAIN_VERIFY, OperationResult.SUCCESS, "证据链校验：" + verified, servletRequest);
        return verified;
    }

    @Override
    public List<EvidenceOperationLogVO> logs(Long id) {
        requireEvidence(id);
        return operationLogMapper.selectList(new LambdaQueryWrapper<EvidenceOperationLog>()
                        .eq(EvidenceOperationLog::getEvidenceId, id)
                        .orderByDesc(EvidenceOperationLog::getCreatedAt))
                .stream()
                .map(log -> EvidenceOperationLogVO.builder()
                        .id(log.getId())
                        .operationType(log.getOperationType())
                        .operatorId(log.getOperatorId())
                        .operatorRole(log.getOperatorRole())
                        .requestIp(log.getRequestIp())
                        .operationResult(log.getOperationResult())
                        .operationDetail(log.getOperationDetail())
                        .createdAt(log.getCreatedAt())
                        .build())
                .toList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public EvidenceVO freeze(Long id, EvidenceFreezeRequest request, HttpServletRequest servletRequest) {
        EvidenceFile file = requireEvidence(id);
        file.setStatus(EvidenceStatus.FROZEN.name());
        evidenceFileMapper.updateById(file);
        evidenceChainService.append(id, EvidenceChainEventType.FROZEN, request.getReason());
        auditRecorder.record(id, EvidenceOperationType.FREEZE, OperationResult.SUCCESS, request.getReason(), servletRequest);
        return toVO(file, findHash(id));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id, HttpServletRequest servletRequest) {
        EvidenceFile file = requireEvidence(id);
        if (EvidenceStatus.FROZEN.name().equals(file.getStatus())) {
            throw new BusinessException(ResultCode.CONFLICT, "冻结证据不允许删除");
        }
        file.setStatus(EvidenceStatus.DELETED.name());
        file.setDeletedAt(LocalDateTime.now());
        evidenceFileMapper.updateById(file);
        evidenceFileMapper.deleteById(id);
        evidenceChainService.append(id, EvidenceChainEventType.DELETED, "逻辑删除证据");
        auditRecorder.record(id, EvidenceOperationType.DELETE, OperationResult.SUCCESS, "逻辑删除证据", servletRequest);
    }

    /**
     * 查询证据，不存在则抛出异常。
     *
     * @param id 证据主键
     * @return 证据实体
     */
    private EvidenceFile requireEvidence(Long id) {
        EvidenceFile file = evidenceFileMapper.selectById(id);
        if (file == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "证据不存在");
        }
        return file;
    }

    /**
     * 查询证据 Hash，不存在则抛出异常。
     *
     * @param evidenceId 证据主键
     * @return Hash 实体
     */
    private EvidenceHash requireHash(Long evidenceId) {
        EvidenceHash hash = findHash(evidenceId);
        if (hash == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "证据Hash不存在");
        }
        return hash;
    }

    /**
     * 查询证据 Hash。
     *
     * @param evidenceId 证据主键
     * @return Hash 实体
     */
    private EvidenceHash findHash(Long evidenceId) {
        return evidenceHashMapper.selectOne(new LambdaQueryWrapper<EvidenceHash>()
                .eq(EvidenceHash::getEvidenceId, evidenceId)
                .last("LIMIT 1"));
    }

    /**
     * 生成证据编号。
     *
     * @return 证据编号
     */
    private String generateEvidenceNo() {
        return "EV-" + LocalDate.now().format(DATE_FORMATTER) + "-" + System.nanoTime();
    }

    /**
     * 构造原始文件对象键。
     *
     * @param caseId     案件编号
     * @param evidenceNo 证据编号
     * @param hash       文件 Hash
     * @param fileExt    文件扩展名
     * @return object key
     */
    private String buildOriginalObjectKey(Long caseId, String evidenceNo, String hash, String fileExt) {
        LocalDate now = LocalDate.now();
        String ext = StringUtils.hasText(fileExt) ? "." + fileExt : "";
        return "%d/%02d/%02d/case-%d/%s/original-%s%s".formatted(
                now.getYear(), now.getMonthValue(), now.getDayOfMonth(), caseId, evidenceNo, hash, ext);
    }

    /**
     * 构造固化凭证对象键。
     *
     * @param caseId     案件编号
     * @param evidenceId 证据编号
     * @return object key
     */
    private String buildSealObjectKey(Long caseId, Long evidenceId) {
        LocalDate now = LocalDate.now();
        return "%d/%02d/%02d/case-%d/evidence-%d/seal-certificate.pdf".formatted(
                now.getYear(), now.getMonthValue(), now.getDayOfMonth(), caseId, evidenceId);
    }

    /**
     * 解析文件扩展名。
     *
     * @param fileName 文件名
     * @return 扩展名
     */
    private String resolveFileExt(String fileName) {
        if (!StringUtils.hasText(fileName) || !fileName.contains(".")) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf('.') + 1);
    }

    /**
     * 转换证据响应视图。
     *
     * @param file 证据实体
     * @param hash Hash 实体
     * @return 证据响应
     */
    private EvidenceVO toVO(EvidenceFile file, EvidenceHash hash) {
        return EvidenceVO.builder()
                .id(file.getId())
                .evidenceNo(file.getEvidenceNo())
                .caseId(file.getCaseId())
                .uploaderId(file.getUploaderId())
                .fileName(file.getFileName())
                .fileExt(file.getFileExt())
                .contentType(file.getContentType())
                .fileSize(file.getFileSize())
                .status(file.getStatus())
                .description(file.getDescription())
                .uploadedAt(file.getUploadedAt())
                .sealedAt(file.getSealedAt())
                .hash(hash == null ? null : toHashVO(hash))
                .build();
    }

    /**
     * 转换 Hash 响应视图。
     *
     * @param hash Hash 实体
     * @return Hash 响应
     */
    private EvidenceHashVO toHashVO(EvidenceHash hash) {
        return EvidenceHashVO.builder()
                .evidenceId(hash.getEvidenceId())
                .hashAlgorithm(hash.getHashAlgorithm())
                .hashValue(hash.getHashValue())
                .fileFingerprint(hash.getFileFingerprint())
                .fileSize(hash.getFileSize())
                .calculatedAt(hash.getCalculatedAt())
                .verifiedAt(hash.getVerifiedAt())
                .verifyResult(hash.getVerifyResult())
                .build();
    }

    /**
     * 转换证据链响应视图。
     *
     * @param chain 证据链实体
     * @return 证据链响应
     */
    private EvidenceChainVO toChainVO(EvidenceChain chain) {
        return EvidenceChainVO.builder()
                .id(chain.getId())
                .chainSeq(chain.getChainSeq())
                .eventType(chain.getEventType())
                .eventDataHash(chain.getEventDataHash())
                .previousChainHash(chain.getPreviousChainHash())
                .currentChainHash(chain.getCurrentChainHash())
                .operatorId(chain.getOperatorId())
                .operatorRole(chain.getOperatorRole())
                .eventTime(chain.getEventTime())
                .timestampValue(chain.getTimestampValue())
                .remark(chain.getRemark())
                .build();
    }
}
