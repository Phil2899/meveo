/*
 * (C) Copyright 2018-2020 Webdrone SAS (https://www.webdrone.fr/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. This program is
 * not suitable for any direct or indirect application in MILITARY industry See the GNU Affero
 * General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 */

package org.meveo.api.git;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseCrudApi;
import org.meveo.api.dto.git.GitRepositoryDto;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.commons.utils.FileUtils;
import org.meveo.exceptions.EntityDoesNotExistsException;
import org.meveo.model.git.GitRepository;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.git.GitClient;
import org.meveo.service.git.GitHelper;
import org.meveo.service.git.GitRepositoryService;
import org.meveo.service.git.MeveoRepository;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

/**
 * API for managing git repositories
 * @author Clement Bareth
 * @lastModifiedVersion 6.4.0
 */
@Stateless
public class GitRepositoryApi extends BaseCrudApi<GitRepository, GitRepositoryDto> {

    @Inject
    private GitRepositoryService gitRepositoryService;

    @Inject
    private GitClient gitClient;

    @Inject
    @MeveoRepository
    private GitRepository meveoRepository;

    public GitRepositoryApi() {
        super(GitRepository.class, GitRepositoryDto.class);
    }

    /**
     * Import the given zip file in the file system.
     * Create corresponding {@link GitRepository} if repo does not exists yet or override is true
     *
     * @param inputStream      ZIP file
     * @param gitRepositoryDto {@link GitRepository} DTO representation
     * @param override         Whether to delete existing data
     */
    public void importZip(InputStream inputStream, GitRepositoryDto gitRepositoryDto, boolean override) throws Exception {
        if(gitRepositoryDto.getCode().equals(meveoRepository.getCode())){
            throw new IllegalAccessException("Cannot import Meveo default directory");
        }

        boolean exists = exists(gitRepositoryDto);
        if(exists && !override){
            return;
        }

        if(exists){
            remove(gitRepositoryDto.getCode());
            gitRepositoryService.flush();
        }

        File repositoryDir = GitHelper.getRepositoryDir(currentUser, gitRepositoryDto.getCode());
        if(repositoryDir.exists()) {
            org.apache.commons.io.FileUtils.deleteDirectory(repositoryDir);
        }

        FileUtils.unzipFile(repositoryDir.getAbsolutePath(), inputStream);
        File dotGitDir = new File(repositoryDir, ".git");
        if(dotGitDir.exists()) {
            org.apache.commons.io.FileUtils.deleteDirectory(dotGitDir);
        }

        create(gitRepositoryDto, false, null, null);
    }

    /**
     * Zip the folder of a {@link GitRepository},
     * switching to the given branch then return to original branch if needed.
     *
     * @param code Code of the repository to export
     * @param branch Branch to export. Will take current branch if not provided.
     * @return the zipped folder corresponding to the given {@link GitRepository}
     */
    public byte[] exportZip(String code, String branch) throws Exception {
        final GitRepository repository = code.equals(meveoRepository.getCode()) ? meveoRepository : gitRepositoryService.findByCode(code);
        String currentBranch = gitClient.currentBranch(repository);

        try {
            // Switch to branch to export if needed
            if (branch != null) {
                if (!currentBranch.equals(branch)) {
                    gitClient.checkout(repository, branch, false);
                }
            }

            final File repositoryDir = GitHelper.getRepositoryDir(currentUser, code);
            return FileUtils.createZipFile(repositoryDir.getAbsolutePath());

        } finally {
            // Return to original branch if needed
            if (!currentBranch.equals(branch)) {
                gitClient.checkout(repository, currentBranch, false);
            }
        }
    }

    public List<GitRepositoryDto> list() {
        return gitRepositoryService.list()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public void remove(String code) throws BusinessException {
        final GitRepository repository = gitRepositoryService.findByCode(code);
        gitRepositoryService.remove(repository);
    }

    @Override
    public GitRepositoryDto toDto(GitRepository entity) {
        GitRepositoryDto dto = new GitRepositoryDto();
        dto.setReadingRoles(entity.getReadingRoles());
        dto.setWritingRoles(entity.getWritingRoles());
        dto.setRemoteOrigin(entity.getRemoteOrigin());
        dto.setRemoteUsername(entity.getDefaultRemoteUsername());
        dto.setRemotePassword(entity.getDefaultRemotePassword());
        dto.setCode(entity.getCode());
        dto.setDescription(entity.getDescription());
        dto.setMeveoRepository(entity.isMeveoRepository());
        dto.setCurrentBranch(entity.getCurrentBranch());
        dto.setBranches(entity.getBranches());
        return dto;
    }

    @Override
    public GitRepository fromDto(GitRepositoryDto dto) throws EntityDoesNotExistsException {
        GitRepository entity = new GitRepository();
        entity.setRemoteOrigin(dto.getRemoteOrigin());
        entity.setCode(dto.getCode());

        updateEntity(dto, entity);

        return entity;
    }

    @Override
    public IPersistenceService<GitRepository> getPersistenceService() {
        return gitRepositoryService;
    }

    @Override
    public boolean exists(GitRepositoryDto dto) {
        try {
            return find(dto.getCode()) != null;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public GitRepositoryDto find(String code) throws MeveoApiException, EntityDoesNotExistsException {
        final GitRepository repository = code.equals(meveoRepository.getCode()) ? meveoRepository : gitRepositoryService.findByCode(code);
        return toDto(repository);
    }

    @Override
    public GitRepository createOrUpdate(GitRepositoryDto dtoData) throws MeveoApiException, BusinessException {
        return exists(dtoData) ? update(dtoData) : create(dtoData, true, null, null);
    }

    public GitRepository create(GitRepositoryDto dtoData, boolean failIfExist, String username, String password) throws BusinessException {
        final GitRepository repository = fromDto(dtoData);
        gitRepositoryService.create(repository, failIfExist, username, password);
        return repository;
    }

    public GitRepository update(GitRepositoryDto dto) throws BusinessException {
        final GitRepository entity = gitRepositoryService.findByCode(dto.getCode());

        if(dto.getRemoteOrigin() != null && entity.getRemoteOrigin() != null && !dto.getRemoteOrigin().equals(entity.getRemoteOrigin())) {
            throw new IllegalArgumentException("Cannot update remote origin of GitRepository " + dto.getCode());
        }

        updateEntity(dto, entity);

        gitRepositoryService.update(entity);

        return entity;
    }

    private void updateEntity(GitRepositoryDto dto, GitRepository entity) {
        entity.setReadingRoles(dto.getReadingRoles());
        entity.setWritingRoles(dto.getWritingRoles());
        entity.setDefaultRemoteUsername(dto.getRemoteUsername());
        entity.setDefaultRemotePassword(dto.getRemotePassword());
        entity.setDescription(dto.getDescription());
        entity.setMeveoRepository(dto.isMeveoRepository());
    }
}