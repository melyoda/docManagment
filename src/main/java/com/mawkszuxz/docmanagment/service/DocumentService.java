package com.mawkszuxz.docmanagment.service;

import com.mawkszuxz.docmanagment.model.DocumentMetadata;
import com.mawkszuxz.docmanagment.repository.DocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DocumentService {

    @Autowired
    private DocumentRepository documentRepository;

    public void saveDoc(DocumentMetadata doc) {
        documentRepository.save(doc);
    }
}
