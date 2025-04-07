package com.github.stabrinai.geoipplayer.service;


import com.maxmind.db.CHMCache;
import com.maxmind.db.Reader;
import com.github.stabrinai.geoipplayer.GeoIPPlayer;
import com.github.stabrinai.geoipplayer.record.LookupResult;
import com.github.stabrinai.geoipplayer.util.InternetProtocolUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileTime;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;

public class GeoIPService {
    private final GeoIPPlayer plugin;
    private final Path DatabaseFilePath;
    private final Logger logger;
    private static final String LICENSE =
            "[LICENSE] This product includes GeoLite2 data created by MaxMind, available at https://www.maxmind.com";

    private Reader reader;

    private volatile boolean downloading;

    public GeoIPService(GeoIPPlayer plugin) {
        this.plugin = plugin;
        this.DatabaseFilePath = plugin.getDataDirectory().resolve("GeoLite2-City.mmdb");
        this.logger = plugin.getLogger();
        isDataAvailable();
    }

    private synchronized void isDataAvailable() {
        if (downloading) {
            return;
        }

        if (reader != null) {
            return;
        }

        if (Files.exists(DatabaseFilePath)) {
            if (plugin.getConfigData().node("geolite2.update_interval_days").getInt(30) < 7) {
                return;
            }
            try {
                FileTime lastModifiedTime = Files.getLastModifiedTime(DatabaseFilePath);
                if (Duration.between(lastModifiedTime.toInstant(), Instant.now()).toDays() <= plugin.getConfigData().node("geolite2", "update_interval_days").getInt(30)) {
                    startReading();
                    return;
                } else {
                    logger.info("GEO IP database is older than {} days", plugin.getConfigData().node("geolite2", "update_interval_days").getInt(30));
                }
            } catch (IOException ioEx) {
                logger.warn("Failed to load GeoLiteAPI database", ioEx);
                return;
            }
        }

        downloading = true;
        plugin.getServer().getScheduler().buildTask(plugin, task -> updateDatabase()).schedule();
    }

    private void updateDatabase() {
        try (InputStream file = URI.create(plugin.getConfigData().node("geolite2", "download_url").getString("")).toURL().openStream()) {
            if (Files.exists(DatabaseFilePath)) Files.delete(DatabaseFilePath);
            Files.copy(new BufferedInputStream(file), DatabaseFilePath, StandardCopyOption.REPLACE_EXISTING);
            logger.info("Successfully downloaded new GEO IP database to {}", DatabaseFilePath);
            startReading();
        } catch (IOException ioEx) {
            logger.warn("Could not download GeoLiteAPI database", ioEx);
        }
    }


    private void startReading() throws IOException {
        // 读取数据库
        reader = new Reader(new File(DatabaseFilePath.toString()), Reader.FileMode.MEMORY, new CHMCache());
        // logger.info(LICENSE);
        downloading = false;
    }

    public HashMap<String, String> lookupByIp(InetSocketAddress inetSocketAddress) {
        HashMap<String, String> result = new HashMap<>();
        String ipAddress = inetSocketAddress.getAddress().getHostAddress();
        String language = plugin.getConfigData().node("geolite2", "language").getString("");

        result.put("ip", inetSocketAddress.toString());
        // 数据库初始化失败
        if (reader == null)
            result.put("ERROR", plugin.getConfigData().node("messages", "database_error").getString(""));
        // 本机或环回地址
        if (InternetProtocolUtils.isLocalAddress(ipAddress))
            result.put("ERROR", plugin.getConfigData().node("messages", "local_address").getString(""));
        // 本机或数据库未初始化时返回
        if (result.containsKey("ERROR"))
            return result;
        // 开始查询
        LookupResult data = null;
        try {
            data = reader.get(InetAddress.getByName(ipAddress), LookupResult.class);
        } catch (IOException ignored) {
        }

        if (data != null) {
            // 板块
            result.put("continent", getNames(data.continent().names(), language));
            // 国家
            result.put("country", getNames(data.country().names(), language));
            // 当无法识别城市时显示国家
            if (data.city() != null)
                result.put("city", getNames(data.city().names(), language));
            else
                result.put("city", getNames(data.country().names(), language));
            // 时区
            result.put("time_zone", data.location().time_zone());
            // 经纬度
            result.put("location", data.location().longitude() + ", " + data.location().latitude());

        }
        return result;
    }

    private String getNames(Map<String, String> data, String language) {
        if (data == null || language == null) return null;
        if (data.get(language) == null) return data.get("en");
        return data.get(language);
    }
}
