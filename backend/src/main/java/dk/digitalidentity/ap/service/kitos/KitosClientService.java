package dk.digitalidentity.ap.service.kitos;

import dk.kitos.api.ApiV2ItSystemApi;
import dk.kitos.api.model.ItSystemResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class KitosClientService {
    private final ApiV2ItSystemApi itSystemApi;

    static final Integer PAGE_SIZE = 100;
    static final Integer MAX_PAGE_REQUEST = 50;

    public KitosClientService(final ApiV2ItSystemApi itSystemApi) {
        this.itSystemApi = itSystemApi;
    }
    public List<ItSystemResponseDTO> listSystems() {
        final List<ItSystemResponseDTO> allSystems = new ArrayList<>();
        List<ItSystemResponseDTO> currentSystems;
        int page = 0;
        do {
            currentSystems = itSystemApi.getManyItSystemV2GetItSystems(null, null, null, null, null, false, null, null, null, null, page++, PAGE_SIZE);
            allSystems.addAll(currentSystems);
        } while (currentSystems.size() == PAGE_SIZE && page < MAX_PAGE_REQUEST);
        return allSystems;
    }
}
