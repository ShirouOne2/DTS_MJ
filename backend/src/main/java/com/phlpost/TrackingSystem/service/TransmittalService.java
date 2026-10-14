package com.phlpost.TrackingSystem.service;

import com.phlpost.TrackingSystem.dto.response.TransmittalDto;
import com.phlpost.TrackingSystem.entity.Transmittal;
import com.phlpost.TrackingSystem.entity.TransactionRecipient;
import com.phlpost.TrackingSystem.exception.NotFoundException;
import com.phlpost.TrackingSystem.repository.TransmittalRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

/** Backs TransmittalView.vue — the system-wide event log, searchable by date. */
@Service
public class TransmittalService {

    private final TransmittalRepository transmittalRepository;

    public TransmittalService(TransmittalRepository transmittalRepository) {
        this.transmittalRepository = transmittalRepository;
    }

    public List<TransmittalDto> search(LocalDate fromDate, LocalDate toDate) {
        return transmittalRepository.findAll().stream()
                .filter(t -> fromDate == null || !t.getCreatedAt().toLocalDate().isBefore(fromDate))
                .filter(t -> toDate == null || !t.getCreatedAt().toLocalDate().isAfter(toDate))
                .sorted(Comparator.comparing(Transmittal::getCreatedAt).reversed())
                .map(this::toDto)
                .toList();
    }

    public TransmittalDto getById(UUID id) {
        return transmittalRepository.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new NotFoundException("Transmittal not found: " + id));
    }

    private TransmittalDto toDto(Transmittal t) {
        String recipientSummary = null;
        if (t.getTransaction() != null) {
            List<TransactionRecipient> recipients = t.getTransaction().getRecipients();
            if (recipients.size() == 1) {
                recipientSummary = recipients.get(0).getDepartment().getName();
            } else if (recipients.size() > 1) {
                recipientSummary = recipients.size() + " departments";
            }
        }
        return new TransmittalDto(
                t.getId(), t.getDocument().getId(), t.getDocument().getReferenceNo(),
                t.getTransaction() != null ? t.getTransaction().getId() : null,
                formatActionLabel(t.getAction()),
                t.getFromDepartment() != null ? t.getFromDepartment().getName() : null,
                recipientSummary, t.getRemarks(), t.getCreatedAt()
        );
    }

    private String formatActionLabel(Transmittal.Action action) {
        String[] words = action.name().split("_");
        StringBuilder sb = new StringBuilder();
        for (String w : words) {
            sb.append(w.charAt(0)).append(w.substring(1).toLowerCase()).append(" ");
        }
        return sb.toString().trim();
    }
}
