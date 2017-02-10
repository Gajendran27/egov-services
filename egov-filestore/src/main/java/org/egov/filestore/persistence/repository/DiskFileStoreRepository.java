package org.egov.filestore.persistence.repository;

import org.egov.filestore.domain.model.Artifact;
import org.egov.filestore.domain.model.FileLocation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
public class DiskFileStoreRepository {

    private FileRepository fileRepository;
    private String fileMountPath;

    public DiskFileStoreRepository(FileRepository fileRepository,
                                   @Value("${fileStorageMountPath}") String fileMountPath) {
        this.fileRepository = fileRepository;
        this.fileMountPath = fileMountPath;
    }

    public void write(List<Artifact> artifacts) {
        artifacts.forEach(artifact -> {
            MultipartFile multipartFile = artifact.getMultipartFile();
            Path path = Paths.get(fileMountPath, artifact.getJurisdictionId(), artifact.getModule(),
                    artifact.getFileStoreId());
            fileRepository.write(multipartFile, path);
        });
    }

    public Resource read(FileLocation fileLocation) {
        Path path = Paths.get(fileMountPath, fileLocation.getJurisdictionId(),
                fileLocation.getModule(), fileLocation.getFileStoreId());
        return fileRepository.read(path);
    }

}

