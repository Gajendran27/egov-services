package org.egov.filestore.persistence.repository;

import org.egov.filestore.domain.model.Resource;
import org.egov.filestore.persistence.entity.Artifact;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class ArtifactRepository {

    private DiskFileStoreRepository diskFileStoreRepository;
    private FileStoreJpaRepository fileStoreJpaRepository;

    public ArtifactRepository(DiskFileStoreRepository diskFileStoreRepository, FileStoreJpaRepository fileStoreJpaRepository) {
        this.diskFileStoreRepository = diskFileStoreRepository;
        this.fileStoreJpaRepository = fileStoreJpaRepository;
    }

    public List<String> save(List<org.egov.filestore.domain.model.Artifact> artifacts) {
        diskFileStoreRepository.write(artifacts);
        List<Artifact> artifactEntities = mapArtifactsListToEntitiesList(artifacts);
        return fileStoreJpaRepository.save(artifactEntities)
                .stream()
                .map(Artifact::getFileStoreId)
                .collect(Collectors.toList());
    }

    private List<Artifact> mapArtifactsListToEntitiesList(List<org.egov.filestore.domain.model.Artifact> artifacts) {
        return artifacts.stream().map(this::mapArtifactToFileStoreMapper).collect(Collectors.toList());
    }

    private Artifact mapArtifactToFileStoreMapper(org.egov.filestore.domain.model.Artifact artifact) {
        Artifact artifactEntity = new Artifact();
        artifactEntity.setFileStoreId(artifact.getFileStoreId());
        artifactEntity.setFileName(artifact.getMultipartFile().getOriginalFilename());
        artifactEntity.setContentType(artifact.getMultipartFile().getContentType());
        return artifactEntity;
    }

    public Resource find(String fileStoreId) {
        Artifact artifact = fileStoreJpaRepository.findByFileStoreId(fileStoreId);
        org.springframework.core.io.Resource resource = diskFileStoreRepository.read(artifact.getFileLocation());
        return new Resource(artifact.getContentType(), artifact.getFileName(), resource);
    }
}
