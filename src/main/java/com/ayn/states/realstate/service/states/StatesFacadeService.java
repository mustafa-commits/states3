//package com.ayn.states.realstate.service.states;
//
//import com.ayn.states.realstate.dto.states.StateSearchCriteriaDTO;
//import com.ayn.states.realstate.dto.states.StatesDTO;
//import com.ayn.states.realstate.dto.states.StatesSummaryDTO;
//import com.ayn.states.realstate.entity.states.States;
//import com.ayn.states.realstate.enums.StateType;
//import com.ayn.states.realstate.mapper.StatesMapper;
//import com.ayn.states.realstate.repository.state.StatesRepo;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.cache.annotation.Cacheable;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageImpl;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Sort;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
///**
// * Facade service for optimized property queries with appropriate DTOs
// */
//@Service
//public class StatesFacadeService {
//
//    @Autowired
//    private StatesRepo statesRepository;
//
//    @Autowired
//    private StatesMapper statesMapper;
//
//    /**
//     * Get properties for sale with lightweight DTOs for listing
//     *
//     * @return List of summary DTOs for properties listed for sale
//     */
////    @Cacheable(value = "SalePropertiesSummary")
////    public Page<StatesSummaryDTO> getPropertiesForSaleSummary() {
////        Page<States> forSaleStates = statesRepository.findByStateTypeAndIsActiveTrue(StateType.FOR_SALE,PageRequest.of(page, 100, Sort.by(Sort.Direction.DESC, "lastSeen")));
////        return statesMapper.toSummaryDtoList(forSaleStates);
////    }
//
//    /**
//     * Get properties for rent with lightweight DTOs for listing
//     *
//     * @return List of summary DTOs for properties listed for rent
//     */
////    @Cacheable(value = "RentPropertiesSummary")
////    public Page<StatesSummaryDTO> getPropertiesForRentSummary() {
////        Page<States> forRentStates = statesRepository.findByStateTypeAndIsActiveTrue(StateType.FOR_RENT,PageRequest.of(0, 20, Sort.by(Sort.Direction.fromString(""), "publishedAt")));
////        return statesMapper.toSummaryDtoList(forRentStates);
////    }
//
//    /**
//     * Get detailed property information by ID
//     *
//     * @param stateId Property ID
//     * @return Detailed DTO with all property information
//     */
//    public StatesDTO getPropertyDetails(long stateId) {
//        return statesRepository.findById(stateId)
//                .map(statesMapper::toDto)
//                .orElse(null);
//    }
//
//    /**
//     * Search properties based on criteria with pagination
//     *
//     * @param criteria Search criteria
//     * @return Page of summary DTOs matching the criteria
//     */
////    public Page<StatesSummaryDTO> searchProperties(StateSearchCriteriaDTO criteria) {
////        // Build sort
////        Sort sort = Sort.by(Sort.Direction.fromString(criteria.getSortDirection()), criteria.getSortBy());
////
////        // Build page request
////        PageRequest pageRequest = PageRequest.of(criteria.getPage(), criteria.getSize(), sort);
////
////        // For this example, we're just returning a simple implementation
////        // In a real application, you'd use a Specification or custom query
////        List<States> states;
////
////        if (criteria.getStateType() != null) {
////            states = statesRepository.findByStateTypeAndIsActiveTrue(criteria.getStateType());
////        } else {
////            states = statesRepository.findAll();
////        }
////
////        List<StatesSummaryDTO> dtos = statesMapper.toSummaryDtoList(states);
////
////        // Simple pagination for example
////        int start = (int) pageRequest.getOffset();
////        int end = Math.min((start + pageRequest.getPageSize()), dtos.size());
////
////        List<StatesSummaryDTO> paginatedList = start < end ? dtos.subList(start, end) : List.of();
////
////        return new PageImpl<>(paginatedList, pageRequest, dtos.size());
////    }
//}
