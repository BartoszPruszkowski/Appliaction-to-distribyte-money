package pl.inz.costshare.server.api;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pl.inz.costshare.server.dto.ReceiptDto;
import pl.inz.costshare.server.security.SecurityUtils;
import pl.inz.costshare.server.service.ReceiptService;

import javax.activation.MimetypesFileTypeMap;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("")
public class ReceiptApi {

    private ReceiptService receiptService;

    public ReceiptApi(ReceiptService receiptService) {
        this.receiptService = receiptService;
    }

    @GetMapping
    public ResponseEntity getMyReceipts() {
        Long myId = SecurityUtils.getUserDetails().getUserId();
        List<ReceiptDto> receipts = receiptService.getReceiptsByUploadUser(myId);
        return ResponseEntity.ok().body(receipts);
    }

    @GetMapping("/receipts/{id}/download")
    public ResponseEntity downloadReceipt(@PathVariable("id") Long id) throws IOException {
        Long myId = SecurityUtils.getUserDetails().getUserId();

        ReceiptDto receiptDto = receiptService.findReceiptById(id);
        if (receiptDto == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Receipt with id: [" + id + "] does not exist");
        }

        Resource resource = new ByteArrayResource(receiptDto.getRawData());

        String contentType = new MimetypesFileTypeMap().getContentType(receiptDto.getFileName());

        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(contentType))
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + receiptDto.getFileName() + "\"")
            .body(resource);
    }

    @PostMapping("/events/{eventId}/receipts")
    public ResponseEntity uploadReceipt(@PathVariable("eventId") Long eventId, @RequestParam("file") MultipartFile uploadFile) throws IOException {
        Long myId = SecurityUtils.getUserDetails().getUserId();

        ReceiptDto receiptDto = new ReceiptDto();
        receiptDto.setUploadUserId(myId);
        receiptDto.setEventId(eventId);
        receiptDto.setFileName(uploadFile.getOriginalFilename());
        receiptDto.setRawData(uploadFile.getBytes());

        ReceiptDto resultReceiptDto = receiptService.createReceipt(receiptDto);
        return ResponseEntity.ok().body(resultReceiptDto);
    }

    @GetMapping("/events/{eventId}/receipts")
    public ResponseEntity getReceiptsForEvent(@PathVariable("eventId") Long eventId) {
        List<ReceiptDto> receipts = receiptService.getReceiptsForEvent(eventId);
        return ResponseEntity.ok().body(receipts);
    }
}
