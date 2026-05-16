package com.tdep.document.controller;

import com.tdep.common.api.Result;
import com.tdep.document.dto.DocumentTemplateListRequest;
import com.tdep.document.dto.DocumentTemplateUploadRequest;
import com.tdep.document.service.DocumentTemplateService;
import com.tdep.document.vo.DocumentTemplateVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping({"/api/template", "/template"})
public class DocumentTemplateController {

    private final DocumentTemplateService documentTemplateService;

    @PostMapping("/upload")
    @PreAuthorize("hasAuthority('document:manage') or hasAuthority('document:template:manage')")
    public Result<DocumentTemplateVO> upload(@Valid @ModelAttribute DocumentTemplateUploadRequest request) {
        return Result.success(documentTemplateService.upload(request));
    }

    @GetMapping("/list")
    @PreAuthorize("hasAuthority('document:manage') or hasAuthority('document:template:manage') or hasAuthority('document:generate')")
    public Result<List<DocumentTemplateVO>> list(@ModelAttribute DocumentTemplateListRequest request) {
        return Result.success(documentTemplateService.list(request));
    }

    @PostMapping("/{id}/enable")
    @PreAuthorize("hasAuthority('document:manage') or hasAuthority('document:template:manage')")
    public Result<DocumentTemplateVO> enable(@PathVariable Long id) {
        return Result.success(documentTemplateService.enable(id));
    }

    @PostMapping("/{id}/disable")
    @PreAuthorize("hasAuthority('document:manage') or hasAuthority('document:template:manage')")
    public Result<DocumentTemplateVO> disable(@PathVariable Long id) {
        return Result.success(documentTemplateService.disable(id));
    }

    @GetMapping("/{id}/preview")
    @PreAuthorize("hasAuthority('document:manage') or hasAuthority('document:template:manage') or hasAuthority('document:generate')")
    public Result<DocumentTemplateVO> preview(@PathVariable Long id) {
        return Result.success(documentTemplateService.preview(id));
    }
}
