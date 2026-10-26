package Medco.PremuimFinancing.security;


import org.springframework.stereotype.Component;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Component
public class LoginAttemptService {

    private final long LOCK_TIME = TimeUnit.MINUTES.toMillis(1); // 15 minutes lock

    private final Map<String, LoginAttempt> attemptsCache = new ConcurrentHashMap<>();

    public void loginSucceeded(String username) {
        attemptsCache.remove(username);
    }

    public void loginFailed(String username) {
        LoginAttempt attempt = attemptsCache.getOrDefault(username, new LoginAttempt());
        attempt.increment();
        attemptsCache.put(username, attempt);
    }

    public boolean isBlocked(String username) {
        LoginAttempt attempt = attemptsCache.get(username);
        if (attempt == null) return false;

        int MAX_ATTEMPTS = 5;
        if (attempt.getCount() >= MAX_ATTEMPTS) {
            if (System.currentTimeMillis() - attempt.getLastAttempt() < LOCK_TIME) {
                return true;
            } else {
                attemptsCache.remove(username); // unlock after lock time
                return false;
            }
        }
        return false;
    }

    private static class LoginAttempt {
        private int count = 0;
        private long lastAttempt = System.currentTimeMillis();

        void increment() {
            count++;
            lastAttempt = System.currentTimeMillis();
        }

        int getCount() {
            return count;
        }

        long getLastAttempt() {
            return lastAttempt;
        }
    }
}

