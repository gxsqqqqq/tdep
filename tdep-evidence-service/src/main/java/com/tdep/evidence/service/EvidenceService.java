package com.tdep.evidence.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tdep.evidence.dto.EvidenceFreezeRequest;
import com.tdep.evidence.dto.EvidencePageRequest;
import com.tdep.evidence.dto.EvidenceSealRequest;
import com.tdep.evidence.dto.EvidenceUploadRequest;
import com.tdep.evidence.dto.EvidenceVerifyRequest;
import com.tdep.evidence.vo.EvidenceChainVO;
import com.tdep.evidence.vo.EvidenceDownloadUrlVO;
import com.tdep.evidence.vo.EvidenceHashVO;
import com.tdep.evidence.vo.EvidenceOperationLogVO;
import com.tdep.evidence.vo.EvidenceVO;
import com.tdep.evidence.vo.EvidenceVerifyVO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 电子证据业务服务。
 */
public interface EvidenceService {

    /**
     * 上传证据文件。
     *
     * @param file    上传文件
     * @param request 上传参数
     * @param servletRequest HTTP 请求
     * @return 证据信息
     */
    EvidenceVO upload(MultipartFile file, EvidenceUploadRequest request, HttpServletRequest servletRequest);

    /**
     * 分页查询证据。
     *
     * @param request 查询参数
     * @return 分页结果
     */
    Page<EvidenceVO> page(EvidencePageRequest request);

    /**
     * 查询证据详情。
     *
     * @param id 证据主键
     * @param servletRequest HTTP 请求
     * @return 证据信息
     */
    EvidenceVO detail(Long id, HttpServletRequest servletRequest);

    /**
     * 查询证据 Hash。
     *
     * @param id 证据主键
     * @param servletRequest HTTP 请求
     * @return Hash 信息
     */
    EvidenceHashVO hash(Long id, HttpServletRequest servletRequest);

    /**
     * 校验证据文件完整性。
     *
     * @param id      证据主键
     * @param request 校验请求
     * @param servletRequest HTTP 请求
     * @return 校验结果
     */
    EvidenceVerifyVO verify(Long id, EvidenceVerifyRequest request, HttpServletRequest servletRequest);

    /**
     * 获取预签名下载地址。
     *
     * @param id 证据主键
     * @param servletRequest HTTP 请求
     * @return 下载地址
     */
    EvidenceDownloadUrlVO downloadUrl(Long id, HttpServletRequest servletRequest);

    /**
     * PDF 固化证据。
     *
     * @param id      证据主键
     * @param request 固化请求
     * @param servletRequest HTTP 请求
     * @return 证据信息
     */
    EvidenceVO seal(Long id, EvidenceSealRequest request, HttpServletRequest servletRequest);

    /**
     * 获取固化凭证预签名下载地址。
     *
     * @param id 证据主键
     * @param servletRequest HTTP 请求
     * @return 下载地址
     */
    EvidenceDownloadUrlVO sealDownloadUrl(Long id, HttpServletRequest servletRequest);

    /**
     * 查询证据链。
     *
     * @param id 证据主键
     * @param servletRequest HTTP 请求
     * @return 证据链
     */
    List<EvidenceChainVO> chains(Long id, HttpServletRequest servletRequest);

    /**
     * 校验证据链完整性。
     *
     * @param id 证据主键
     * @param servletRequest HTTP 请求
     * @return true 表示完整
     */
    Boolean verifyChain(Long id, HttpServletRequest servletRequest);

    /**
     * 查询操作日志。
     *
     * @param id 证据主键
     * @return 操作日志
     */
    List<EvidenceOperationLogVO> logs(Long id);

    /**
     * 冻结证据。
     *
     * @param id      证据主键
     * @param request 冻结请求
     * @param servletRequest HTTP 请求
     * @return 证据信息
     */
    EvidenceVO freeze(Long id, EvidenceFreezeRequest request, HttpServletRequest servletRequest);

    /**
     * 逻辑删除证据。
     *
     * @param id 证据主键
     * @param servletRequest HTTP 请求
     */
    void delete(Long id, HttpServletRequest servletRequest);
}
