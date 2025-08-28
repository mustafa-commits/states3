package com.ayn.states.realstate.service.userAction;

import com.ayn.states.realstate.entity.compound.Compound;
import com.ayn.states.realstate.entity.fav.UserActions;
import com.ayn.states.realstate.entity.states.States;
import com.ayn.states.realstate.entity.user.Users;
import com.ayn.states.realstate.enums.ActionType;
import com.ayn.states.realstate.repository.compound.CompoundRepository;
import com.ayn.states.realstate.repository.state.StatesRepo;
import com.ayn.states.realstate.repository.unRegisteredUsers.UnregisteredUserRepo;
import com.ayn.states.realstate.repository.user.UsersRepo;
import com.ayn.states.realstate.repository.userAction.UserActionRepo;
import com.ayn.states.realstate.service.token.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional
public class ViewTrackingService {

    @Autowired
    private CompoundRepository compoundRepository;

    @Autowired
    private StatesRepo statesRepository;

    @Autowired
    private UserActionRepo userActionsRepository;

    @Autowired
    private UsersRepo usersRepository;

    @Autowired
    private UnregisteredUserRepo unregisteredUserRepository;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private JdbcClient jdbcClient;

    // Configuration for duplicate prevention (in minutes)
    private static final int DUPLICATE_PREVENTION_WINDOW_MINUTES = 10;


    public boolean addCompoundView(Long compoundId, String token) {
        Optional<Compound> compoundOpt = compoundRepository.findById(compoundId);

        if (compoundOpt.isEmpty()) {
            return false;
        }
        Long userId = null;
        if (tokenService.decodeToken(token.substring(7)).getSubject().equals("0")){
            userId=tokenService.decodeToken(token.substring(7)).getClaim("UnRegistered");
        }else
            userId=Long.parseLong(tokenService.decodeToken(token.substring(7)).getSubject());

        Compound compound = compoundOpt.get();
        Users user = null;

        if (userId != null) {
            Optional<Users> userOpt = usersRepository.findById(userId);
            if (userOpt.isPresent()) {
                user = userOpt.get();

                // Check for duplicate views by registered user
                Boolean hasRecentView = jdbcClient.sql("""
                                SELECT CASE WHEN COUNT(*) > 0 THEN TRUE ELSE FALSE END 
                                FROM user_actions u 
                                WHERE u.app_user_id = :userId AND u.state_id = :compoundId 
                                  AND action_type = 'VIEW' 
                                  AND action_time > 10
                                """)
                        .param("userId",userId)
                        .param("compoundId",compoundId)
                        .query(Boolean.class)
                        .single();
            }
        } else {
            // Check for duplicate views by anonymous user (IP + session)
            Boolean hasRecentView = jdbcClient.sql("""
                                SELECT CASE WHEN COUNT(*) > 0 THEN TRUE ELSE FALSE END 
                                FROM user_actions u 
                                WHERE u.unregistered_id = :userId AND u.state_id = :compoundId 
                                  AND action_type = 'VIEW' 
                                  AND action_time > 10
                                """)
                    .param("userId",userId)
                    .param("compoundId",compoundId)
                    .query(Boolean.class)
                    .single();
        }

        // Create and save the view action
        UserActions viewAction = UserActions.createCompoundView(compound, user);
        userActionsRepository.save(viewAction);

        return true;
    }


    public boolean addStateView(Long stateId, String token) {
        Optional<States> stateOpt = statesRepository.findById(stateId);

        if (stateOpt.isEmpty()) {
            return false;
        }

        Long userId = null;
        if (tokenService.decodeToken(token.substring(7)).getSubject().equals("0")){
            userId=tokenService.decodeToken(token.substring(7)).getClaim("UnRegistered");
        }else
            userId=Long.parseLong(tokenService.decodeToken(token.substring(7)).getSubject());


        States state = stateOpt.get();
        Users user = null;

        if (userId != null) {
            Optional<Users> userOpt = usersRepository.findById(userId);
            if (userOpt.isPresent()) {
                user = userOpt.get();

                // Check for duplicate views by registered user
                Boolean hasRecentView = jdbcClient.sql("""
                                SELECT CASE WHEN COUNT(*) > 0 THEN TRUE ELSE FALSE END 
                                FROM user_actions u 
                                WHERE u.app_user_id = :userId AND u.state_id = :compoundId 
                                  AND action_type = 'VIEW' 
                                  AND action_time > 10
                                """)
                        .param("userId",userId)
                        .param("compoundId",stateId)
                        .query(Boolean.class)
                        .single();
            }
        } else {
            // Check for duplicate views by anonymous user (IP + session)
            Boolean hasRecentView = jdbcClient.sql("""
                                SELECT CASE WHEN COUNT(*) > 0 THEN TRUE ELSE FALSE END 
                                FROM user_actions u 
                                WHERE u.app_user_id = :userId AND u.state_id = :compoundId 
                                  AND action_type = 'VIEW' 
                                  AND action_time > 10
                                """)
                    .param("userId",userId)
                    .param("compoundId",stateId)
                    .query(Boolean.class)
                    .single();
        }

        // Create and save the view action
        UserActions viewAction = UserActions.createStateView(state, user);
        userActionsRepository.save(viewAction);

        return true;
    }

    /**
     * Get compound view count (this will be calculated by the @Formula in the entity)
     * @param compoundId The ID of the compound
     * @return view count or -1 if not found
     */
    public int getCompoundViewCount(Long compoundId) {
        Optional<Compound> compoundOpt = compoundRepository.findById(compoundId);
        return compoundOpt.map(Compound::getViewsCount).orElse(-1);
    }

    /**
     * Get state view count (this will be calculated by the @Formula in the entity)
     * @param stateId The ID of the state
     * @return view count or -1 if not found
     */
    public int getStateViewCount(Long stateId) {
        Optional<States> stateOpt = statesRepository.findById(stateId);
        return stateOpt.map(States::getViewsCount).orElse(-1);
    }

    /**
     * Get compound unique view count
     * @param compoundId The ID of the compound
     * @return unique view count or -1 if not found
     */
    public int getCompoundUniqueViewCount(Long compoundId) {
        Optional<Compound> compoundOpt = compoundRepository.findById(compoundId);
        return compoundOpt.map(Compound::getViewsCount).orElse(-1);
    }

    /**
     * Get state unique view count
     * @param stateId The ID of the state
     * @return unique view count or -1 if not found
     */
//    public int getStateUniqueViewCount(Long stateId) {
//        Optional<States> stateOpt = statesRepository.findById(stateId);
//        return stateOpt.map(States::getUniqueViewsCount).orElse(-1);
//    }

    /**
     * Add a favorite action for a compound
     */
    public boolean addCompoundFavorite(Long compoundId, Long userId, String sessionId,
                                       String ipAddress, String userAgent) {
        Optional<Compound> compoundOpt = compoundRepository.findById(compoundId);

        if (compoundOpt.isEmpty()) {
            return false;
        }

        Compound compound = compoundOpt.get();
        Users user = null;

        if (userId != null) {
            Optional<Users> userOpt = usersRepository.findById(userId);
            if (userOpt.isPresent()) {
                user = userOpt.get();
            }
        }

        // Create and save the favorite action
        UserActions favoriteAction = new UserActions();
        favoriteAction.setCompound(compound);
        favoriteAction.setUser(user);
        favoriteAction.setActionType(ActionType.FAVORITE);
//        favoriteAction.setIpAddress(ipAddress);
//        favoriteAction.setUserAgent(userAgent);
//        favoriteAction.setSessionId(sessionId);

        userActionsRepository.save(favoriteAction);
        return true;
    }

    /**
     * Add a favorite action for a state
     */
    public boolean addStateFavorite(Long stateId, Long userId, String sessionId,
                                    String ipAddress, String userAgent) {
        Optional<States> stateOpt = statesRepository.findById(stateId);

        if (stateOpt.isEmpty()) {
            return false;
        }

        States state = stateOpt.get();
        Users user = null;

        if (userId != null) {
            Optional<Users> userOpt = usersRepository.findById(userId);
            if (userOpt.isPresent()) {
                user = userOpt.get();
            }
        }

        // Create and save the favorite action
        UserActions favoriteAction = new UserActions();
        favoriteAction.setState(state);
        favoriteAction.setUser(user);
        favoriteAction.setActionType(ActionType.FAVORITE);
//        favoriteAction.setIpAddress(ipAddress);
//        favoriteAction.setUserAgent(userAgent);
//        favoriteAction.setSessionId(sessionId);

        userActionsRepository.save(favoriteAction);
        return true;
    }

    // Private helper methods for duplicate detection

//    private boolean isDuplicateCompoundView(Users user, Compound compound, String sessionId, String ipAddress) {
//        if (user != null) {
//            // Check by user
//            Optional<UserActions> lastAction = userActionsRepository
//                    .findTopByUserAndCompoundAndActionTypeOrderByActionTimeDesc(user, compound, ActionType.VIEW);
//
//            if (lastAction.isPresent()) {
//                LocalDateTime lastViewTime = lastAction.get().getActionTime();
//                LocalDateTime cutoff = LocalDateTime.now().minusMinutes(DUPLICATE_PREVENTION_WINDOW_MINUTES);
//                return lastViewTime.isAfter(cutoff);
//            }
//        }
//
//        // Check by session ID for anonymous users
//        if (sessionId != null) {
//            Optional<UserActions> lastAction = userActionsRepository
//                    .findTopBySessionIdAndCompoundAndActionTypeOrderByActionTimeDesc(sessionId, compound, ActionType.VIEW);
//
//            if (lastAction.isPresent()) {
//                LocalDateTime lastViewTime = lastAction.get().getActionTime();
//                LocalDateTime cutoff = LocalDateTime.now().minusMinutes(DUPLICATE_PREVENTION_WINDOW_MINUTES);
//                return lastViewTime.isAfter(cutoff);
//            }
//        }
//
//        return false;
//    }

//    private boolean isDuplicateStateView(Users user, States state, String sessionId, String ipAddress) {
//        if (user != null) {
//            // Check by user
//            Optional<UserActions> lastAction = userActionsRepository
//                    .findTopByUserAndStateAndActionTypeOrderByActionTimeDesc(user, state, ActionType.VIEW);
//
//            if (lastAction.isPresent()) {
//                LocalDateTime lastViewTime = lastAction.get().getActionTime();
//                LocalDateTime cutoff = LocalDateTime.now().minusMinutes(DUPLICATE_PREVENTION_WINDOW_MINUTES);
//                return lastViewTime.isAfter(cutoff);
//            }
//        }
//
//        // Check by session ID for anonymous users
//        if (sessionId != null) {
//            Optional<UserActions> lastAction = userActionsRepository
//                    .findTopBySessionIdAndStateAndActionTypeOrderByActionTimeDesc(sessionId, state, ActionType.VIEW);
//
//            if (lastAction.isPresent()) {
//                LocalDateTime lastViewTime = lastAction.get().getActionTime();
//                LocalDateTime cutoff = LocalDateTime.now().minusMinutes(DUPLICATE_PREVENTION_WINDOW_MINUTES);
//                return lastViewTime.isAfter(cutoff);
//            }
//        }
//
//        return false;
//    }

//    private boolean isDuplicateCompoundViewAnonymous(Compound compound, String sessionId, String ipAddress) {
//        if (sessionId != null) {
//            Optional<UserActions> lastAction = userActionsRepository
//                    .findTopBySessionIdAndCompoundAndActionTypeOrderByActionTimeDesc(sessionId, compound, ActionType.VIEW);
//
//            if (lastAction.isPresent()) {
//                LocalDateTime lastViewTime = lastAction.get().getActionTime();
//                LocalDateTime cutoff = LocalDateTime.now().minusMinutes(DUPLICATE_PREVENTION_WINDOW_MINUTES);
//                return lastViewTime.isAfter(cutoff);
//            }
//        }
//
//        // Additional IP-based check for anonymous users
//        if (ipAddress != null) {
//            LocalDateTime cutoff = LocalDateTime.now().minusMinutes(DUPLICATE_PREVENTION_WINDOW_MINUTES);
//            var recentActions = userActionsRepository
//                    .findRecentActionsByIpAndCompound(ipAddress, compound, ActionType.VIEW, cutoff);
//            return !recentActions.isEmpty();
//        }
//
//        return false;
//    }

//    private boolean isDuplicateStateViewAnonymous(States state, String sessionId, String ipAddress) {
//        if (sessionId != null) {
//            Optional<UserActions> lastAction = userActionsRepository
//                    .findTopBySessionIdAndStateAndActionTypeOrderByActionTimeDesc(sessionId, state, ActionType.VIEW);
//
//            if (lastAction.isPresent()) {
//                LocalDateTime lastViewTime = lastAction.get().getActionTime();
//                LocalDateTime cutoff = LocalDateTime.now().minusMinutes(DUPLICATE_PREVENTION_WINDOW_MINUTES);
//                return lastViewTime.isAfter(cutoff);
//            }
//        }
//
//        // Additional IP-based check for anonymous users
//        if (ipAddress != null) {
//            LocalDateTime cutoff = LocalDateTime.now().minusMinutes(DUPLICATE_PREVENTION_WINDOW_MINUTES);
//            var recentActions = userActionsRepository
//                    .findRecentActionsByIpAndState(ipAddress, state, ActionType.VIEW, cutoff);
//            return !recentActions.isEmpty();
//        }
//
//        return false;
//    }

    /**
     * Get analytics data for a compound
     */
//    public CompoundAnalytics getCompoundAnalytics(Long compoundId) {
//        Optional<Compound> compoundOpt = compoundRepository.findById(compoundId);
//        if (compoundOpt.isEmpty()) {
//            return null;
//        }
//
//        Compound compound = compoundOpt.get();
//        return new CompoundAnalytics(
//                compound.getViewsCount(),
//                compound.getUniqueViewsCount(),
//                compound.getFavoritesCount(),
//                compound.getLastViewedAt(),
//                compound.getEngagementScore()
//        );
//    }

    /**
     * Get analytics data for a state
     */
//    public StateAnalytics getStateAnalytics(Long stateId) {
//        Optional<States> stateOpt = statesRepository.findById(stateId);
//        if (stateOpt.isEmpty()) {
//            return null;
//        }
//
//        States state = stateOpt.get();
//        return new StateAnalytics(
//                state.getViewsCount(),
//                state.getUniqueViewsCount(),
//                state.getFavoritesCount(),
//                state.getRecentViewsCount(),
//                state.getLastViewedAt(),
//                state.getEngagementScore(),
//                state.getPopularityTrend()
//        );
//    }

    // Analytics DTOs
    public static class CompoundAnalytics {
        public final int totalViews;
        public final int uniqueViews;
        public final int favorites;
        public final LocalDateTime lastViewedAt;
        public final int engagementScore;

        public CompoundAnalytics(int totalViews, int uniqueViews, int favorites,
                                 LocalDateTime lastViewedAt, int engagementScore) {
            this.totalViews = totalViews;
            this.uniqueViews = uniqueViews;
            this.favorites = favorites;
            this.lastViewedAt = lastViewedAt;
            this.engagementScore = engagementScore;
        }
    }

    public static class StateAnalytics {
        public final int totalViews;
        public final int uniqueViews;
        public final int favorites;
        public final int recentViews;
        public final LocalDateTime lastViewedAt;
        public final int engagementScore;
        public final double popularityTrend;

        public StateAnalytics(int totalViews, int uniqueViews, int favorites, int recentViews,
                              LocalDateTime lastViewedAt, int engagementScore, double popularityTrend) {
            this.totalViews = totalViews;
            this.uniqueViews = uniqueViews;
            this.favorites = favorites;
            this.recentViews = recentViews;
            this.lastViewedAt = lastViewedAt;
            this.engagementScore = engagementScore;
            this.popularityTrend = popularityTrend;
        }
    }
}
