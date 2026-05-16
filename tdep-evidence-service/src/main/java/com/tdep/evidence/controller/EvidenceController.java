package com.tdep.evidence.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tdep.common.api.Result;
import com.tdep.evidence.dto.EvidenceFreezeRequest;
import com.tdep.evidence.dto.EvidencePageRequest;
import com.tdep.evidence.dto.EvidenceSealRequest;
import com.tdep.evidence.dto.EvidenceUploadRequest;
import com.tdep.evidence.dto.EvidenceVerifyRequest;
import com.tdep.evidence.service.EvidenceService;
import com.tdep.evidence.vo.EvidenceChainVO;
import com.tdep.evidence.vo.EvidenceDownloadUrlVO;
import com.tdep.evidence.vo.EvidenceHashVO;
import com.tdep.evidence.vo.EvidenceOperationLogVO;
import com.tdep.evidence.vo.EvidenceVO;
import com.tdep.evidence.vo.EvidenceVerifyVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 可信电子证据接口控制器，只负责请求接收、参数校验和统一响应封装。
 */
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping({"/api/evidences", "/evidences", "/api/evidence", "/evidence"})
public class EvidenceController {

    private final EvidenceService evidenceService;

    /**
     * 上传证据文件。
     *
     * @param file           上传文件
     * @param request        上传表单参数
     * @param servletRequest HTTP 请求
     * @return 证据信息
     */
    @PostMapping("/upload")
    @PreAuthorize("hasAuthority('evidence:upload')")
    public Result<EvidenceVO> upload(@RequestParam("file") MultipartFile file,
                                     @Valid @ModelAttribute EvidenceUploadRequest request,
                                     HttpServletRequest servletRequest) {
        return Result.success(evidenceService.upload(file, request, servletRequest));
    }

    /**
     * 分页查询证据。
     *
     * @param request 查询参数
     * @return 证据分页结果
     */
    @GetMapping
    @PreAuthorize("hasAnyAuthority('evidence:read:self','evidence:read:case','evidence:read:all')")
    public Result<Page<EvidenceVO>> page(@Valid EvidencePageRequest request) {
        return Result.success(evidenceService.page(request));
    }

    /**
     * 查询证据详情。
     *
     * @param id             证据主键
     * @param servletRequest HTTP 请求
     * @return 证据信息
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('evidence:read:self','evidence:read:case','evidence:read:all')")
    public Result<EvidenceVO> detail(@PathVariable Long id, HttpServletRequest servletRequest) {
        return Result.success(evidenceService.detail(id, servletRequest));
    }

    /**
     * 查询证据 Hash。
     *
     * @param id             证据主键
     * @param servletRequest HTTP 请求
     * @return Hash 信息
     */
    @GetMapping("/hash/{id}")
    @PreAuthorize("hasAnyAuthority('evidence:read:self','evidence:read:case','evidence:read:all')")
    public Result<EvidenceHashVO> hash(@PathVariable Long id, HttpServletRequest servletRequest) {
        return Result.success(evidenceService.hash(id, servletRequest));
    }

    /**
     * 校验证据文件完整性。
     *
     * @param id             证据主键
     * @param request        校验请求
     * @param servletRequest HTTP 请求
     * @return 校验结果
     */
    @PostMapping("/{id}/verify")
    @PreAuthorize("hasAuthority('evidence:verify')")
    public Result<EvidenceVerifyVO> verify(@PathVariable Long id,
                                           @RequestBody(required = false) EvidenceVerifyRequest request,
                                           HttpServletRequest servletRequest) {
        return Result.success(evidenceService.verify(id, request, servletRequest));
    }

    /**
     * 获取原始证据预签名下载地址。
     *
     * @param id             证据主键
     * @param servletRequest HTTP 请求
     * @return 预签名下载地址
     */
    @GetMapping("/{id}/download-url")
    @PreAuthorize("hasAuthority('evidence:download')")
    public Result<EvidenceDownloadUrlVO> downloadUrl(@PathVariable Long id, HttpServletRequest servletRequest) {
        return Result.success(evidenceService.downloadUrl(id, servletRequest));
    }

    /**
     * 生成 PDF 固化凭证。
     *
     * @param id             证据主键
     * @param request        固化请求
     * @param servletRequest HTTP 请求
     * @return 证据信息
     */
    @PostMapping("/{id}/seal")
    @PreAuthorize("hasAuthority('evidence:seal')")
    public Result<EvidenceVO> seal(@PathVariable Long id,
                                   @RequestBody(required = false) EvidenceSealRequest request,
                                   HttpServletRequest servletRequest) {
        return Result.success(evidenceService.seal(id, request, servletRequest));
    }

    /**
     * 获取 PDF 固化凭证预签名下载地址。
     *
     * @param id             证据主键
     * @param servletRequest HTTP 请求
     * @return 预签名下载地址
     */
    @GetMapping("/{id}/seal/download-url")
    @PreAuthorize("hasAuthority('evidence:download')")
    public Result<EvidenceDownloadUrlVO> sealDownloadUrl(@PathVariable Long id, HttpServletRequest servletRequest) {
        return Result.success(evidenceService.sealDownloadUrl(id, servletRequest));
    }

    /**
     * 查询证据链。
     *
     * @param id             证据主键
     * @param servletRequest HTTP 请求
     * @return 证据链列表
     */
    @GetMapping("/{id}/chains")
    @PreAuthorize("hasAuthority('evidence:chain:read')")
    public Result<List<EvidenceChainVO>> chains(@PathVariable Long id, HttpServletRequest servletRequest) {
        return Result.success(evidenceService.chains(id, servletRequest));
    }

    /**
     * 校验证据链完整性。
     *
     * @param id             证据主键
     * @param servletRequest HTTP 请求
     * @return true 表示证据链完整
     */
    @PostMapping("/{id}/chains/verify")
    @PreAuthorize("hasAuthority('evidence:chain:read')")
    public Result<Boolean> verifyChain(@PathVariable Long id, HttpServletRequest servletRequest) {
        return Result.success(evidenceService.verifyChain(id, servletRequest));
    }

    /**
     * 查询证据操作日志。
     *
     * @param id 证据主键
     * @return 操作日志列表
     */
    @GetMapping("/{id}/logs")
    @PreAuthorize("hasAuthority('evidence:audit:read')")
    public Result<List<EvidenceOperationLogVO>> logs(@PathVariable Long id) {
        return Result.success(evidenceService.logs(id));
    }

    /**
     * 冻结证据。
     *
     * @param id             证据主键
     * @param request        冻结请求
     * @param servletRequest HTTP 请求
     * @return 证据信息
     */
    @PostMapping("/{id}/freeze")
    @PreAuthorize("hasAuthority('evidence:freeze')")
    public Result<EvidenceVO> freeze(@PathVariable Long id,
                                     @Valid @RequestBody EvidenceFreezeRequest request,
                                     HttpServletRequest servletRequest) {
        return Result.success(evidenceService.freeze(id, request, servletRequest));
    }

    /**
     * 逻辑删除证据。
     *
     * @param id             证据主键
     * @param servletRequest HTTP 请求
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('evidence:delete')")
    public Result<Void> delete(@PathVariable Long id, HttpServletRequest servletRequest) {
        evidenceService.delete(id, servletRequest);
        return Result.success();
    }
}
