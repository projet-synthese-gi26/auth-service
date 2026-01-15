package com.tramasys.auth.adapters.out.external;

import com.tramasys.auth.adapters.out.external.dto.MediaResponseDto;
import com.tramasys.auth.domain.exception.DomainException;
import com.tramasys.auth.domain.model.TramasysService;
import com.tramasys.auth.domain.port.out.MediaStoragePort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;

import java.util.Locale;
import java.util.UUID;

@Component
public class HttpMediaStorageAdapter implements MediaStoragePort {

    private final RestClient restClient;

    public HttpMediaStorageAdapter(@Value("${media.service.url}") String mediaServiceUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(mediaServiceUrl)
                .build();
    }

    @Override
    public MediaResult upload(MultipartFile file, TramasysService service) {
        try {
            // Construction du corps Multipart
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", convertToFileResource(file));
            body.add("service", formatServiceName(service));
            body.add("location", "profiles"); // Dossier cible dans le bucket

            MediaResponseDto response = restClient.post()
                    .uri("/media/upload")
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(body)
                    .retrieve()
                    .body(MediaResponseDto.class);

            if (response == null) {
                throw new DomainException("Media Service returned an empty response during upload");
            }
            return new MediaResult(response.id(), response.uri());

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            throw new DomainException("Media Service Error (" + e.getStatusCode() + "): " + e.getResponseBodyAsString(),
                    e);
        } catch (Exception e) {
            throw new DomainException("Failed to communicate with Media Service: " + e.getMessage(), e);
        }
    }

    @Override
    public MediaResult replace(UUID mediaId, MultipartFile file) {
        try {
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", convertToFileResource(file));
            // Note: Le paramètre 'location' est optionnel sur le PUT du Media Service,
            // on ne l'envoie pas pour conserver l'emplacement actuel.

            MediaResponseDto response = restClient.put()
                    .uri("/media/{id}", mediaId)
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(body)
                    .retrieve()
                    .body(MediaResponseDto.class);

            if (response == null) {
                throw new DomainException("Media Service returned an empty response during replacement");
            }
            return new MediaResult(response.id(), response.uri());

        } catch (HttpClientErrorException.NotFound e) {
            // Si l'ancienne image n'existe plus côté Media, on tente un upload classique
            // pour récupérer
            // Mais pour l'instant, on lève une exception claire.
            throw new DomainException("Original media not found in Media Service. Cannot replace.", e);
        } catch (Exception e) {
            throw new DomainException("Failed to replace media: " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(UUID mediaId) {
        try {
            restClient.delete()
                    .uri("/media/{id}", mediaId)
                    .retrieve()
                    .toBodilessEntity();
        } catch (HttpClientErrorException.NotFound e) {
            // Ignorer si déjà supprimé
        } catch (Exception e) {
            // On log seulement, pour ne pas bloquer une transaction de suppression
            // utilisateur
            System.err.println("Warning: Failed to delete media " + mediaId + " : " + e.getMessage());
        }
    }

    /**
     * Convertit MultipartFile en Resource compatible avec RestClient.
     * CRITIQUE : Il faut surcharger getFilename(), sinon WebFlux (Media Service)
     * rejettera le fichier.
     */
    private Resource convertToFileResource(MultipartFile file) throws Exception {
        return new ByteArrayResource(file.getBytes()) {
            @Override
            public String getFilename() {
                // Si le fichier d'origine n'a pas de nom, on en invente un pour satisfaire le
                // serveur
                return file.getOriginalFilename() != null && !file.getOriginalFilename().isEmpty()
                        ? file.getOriginalFilename()
                        : "profile_pic_" + UUID.randomUUID() + ".jpg";
            }
        };
    }

    // "RIDE_AND_GO" -> "rideandgo"
    private String formatServiceName(TramasysService service) {
        if (service == null)
            return "common";
        return service.name().toLowerCase(Locale.ROOT).replace("_", "");
    }
}