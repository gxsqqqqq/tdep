package com.tdep.document.controller;

import com.tdep.common.api.Result;
import com.tdep.document.dto.DocumentExportPdfRequest;
import com.tdep.document.dto.DocumentGenerateRequest;
import com.tdep.document.dto.DocumentSignRequest;
import com.tdep.document.enums.DocumentFileFormat;
import com.tdep.document.service.DocumentService;
import com.tdep.document.vo.DocumentArchiveVO;
import com.tdep.document.vo.DocumentDownloadVO;
import com.tdep.document.vo.DocumentFileVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping({"/api/document", "/document"})
public class DocumentController {

    private final DocumentService documentService;

    @PostMapping("/generate")
    @PreAuthorize("hasAuthority('document:manage') or hasAuthority('document:generate')")
    public Result<DocumentFileVO> generate(@Valid @RequestBody DocumentGenerateRequest request) {
        return Result.success(documentService.generate(request));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('document:manage') or hasAuthority('document:download')")
    public Result<DocumentFileVO> getById(@PathVariable Long id) {
        return Result.success(documentService.getById(id));
    }

    @PostMapping("/export/pdf")
    @PreAuthorize("hasAuthority('document:manage') or hasAuthority('document:generate')")
    public Result<DocumentFileVO> exportPdf(@Valid @RequestBody DocumentExportPdfRequest request) {
        return Result.success(documentService.exportPdf(request));
    }

    @PostMapping("/sign")
    @PreAuthorize("hasAuthority('document:manage') or hasAuthority('document:sign')")
    public Result<DocumentFileVO> sign(@Valid @RequestBody DocumentSignRequest request) {
        return Result.success(documentService.sign(request));
    }

    @GetMapping("/download/{id}")
    @PreAuthorize("hasAuthority('document:manage') or hasAuthority('document:download')")
    public Result<DocumentDownloadVO> download(@PathVariable Long id,
                                               @RequestParam(defaultValue = "SIGNED_PDF") DocumentFileFormat format) {
        return Result.success(documentService.download(id, format));
    }

    @GetMapping("/preview/{id}")
    @PreAuthorize("hasAuthority('document:manage') or hasAuthority('document:download')")
    public Result<DocumentDownloadVO> previewPdf(@PathVariable Long id) {
        return Result.success(documentService.previewPdf(id));
    }

    @PostMapping("/{id}/archive")
    @PreAuthorize("hasAuthority('document:manage') or hasAuthority('document:archive')")
    public Result<DocumentArchiveVO> archive(@PathVariable Long id) {
        return Result.success(documentService.archive(id));
    }

    @GetMapping("/{id}/sign/verify")
    @PreAuthorize("hasAuthority('document:manage') or hasAuthority('document:sign') or hasAuthority('document:download')")
    public Result<Boolean> verifySign(@PathVariable Long id) {
        return Result.success(documentService.verifySign(id));
    }
}
