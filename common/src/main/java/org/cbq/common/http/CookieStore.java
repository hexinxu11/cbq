package org.cbq.common.http;

import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieIdentityComparator;

import java.util.Date;
import java.util.Optional;
import java.util.TreeSet;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class CookieStore {
    private static final long serialVersionUID = -7581093305228232025L;
    private final static TreeSet<Cookie> cookies = new TreeSet(new CookieIdentityComparator());


    public static void addCookie(Cookie cookie) {
        if (cookie != null) {
            ReadWriteLock lock = new ReentrantReadWriteLock();
            lock.writeLock().lock();
            try {
                refreshr(cookie);
                if (!cookie.isExpired(new Date())) {
                    cookies.add(cookie);
                }
            } finally {
                lock.writeLock().unlock();
            }
        }

    }

    private static void refreshr(Cookie cookie){
      Optional<Cookie> c1 =  cookies.stream().filter(f->f.getName().equals(cookie.getName())).findFirst();
      if (c1.isPresent()){
          cookies.remove(c1.get());
      }
    }

    public static Cookie getCookie(String userId) {
        if (null!=userId){
            ReadWriteLock lock = new ReentrantReadWriteLock();
            lock.readLock().lock();
            try {
                Optional<Cookie> cookie = cookies.stream().filter(f->f.getName().equals(userId)).findFirst();
                if (cookie.isPresent()){
                    return cookie.get();
                }

            } finally {
                lock.readLock().unlock();
            }

        }
        return null;
    }
}
