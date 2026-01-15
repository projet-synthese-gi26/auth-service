package com.tramasys.auth.domain.port.out;

import com.tramasys.auth.domain.model.TramasysService;
import org.springframework.web.multipart.MultipartFile;
import java.util.UUID;

public interface MediaStoragePort {
    /**
     * Upload a new file.
     * @return A simple DTO containing the ID and URI from the media service.
     */
    MediaResult upload(MultipartFile file, TramasysService service);

    /**
     * Replace an existing file.
     * @return A simple DTO containing the updated info.
     */
    MediaResult replace(UUID mediaId, MultipartFile file);

    /**
     * Delete a file.
     */
    void delete(UUID mediaId);

    // Record simple pour transporter le résultat dans le domaine sans dépendre du JSON externe
    record MediaResult(UUID id, String uri) {}
}
