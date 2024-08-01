package com.pay2ply.sdk;

import com.google.gson.Gson;
import com.pay2ply.sdk.dispense.Dispense;

import javax.net.ssl.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SDK {
    private static final Logger LOGGER = Logger.getLogger(SDK.class.getName());
    private static final ExecutorService executorService = Executors.newCachedThreadPool();
    private static final int CONNECT_TIMEOUT = 5000;
    private static final int READ_TIMEOUT = 10000;
    private String API = "https://api.pay2ply.com/";
    private String token;

    public String getAPI() {
        return API;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public CompletableFuture<Dispense[]> getDispenses() {
        return get().thenApply(response -> new Gson().fromJson(response, Dispense[].class));
    }

    public CompletableFuture<Void> updateDispense(String username, int id) {
        return update(username, id);
    }

    public CompletableFuture<String> get() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                TrustManager[] trustAllCerts = new TrustManager[]{
                        new X509TrustManager() {
                            public X509Certificate[] getAcceptedIssuers() {
                                return null;
                            }

                            public void checkClientTrusted(X509Certificate[] certs, String authType) {
                            }

                            public void checkServerTrusted(X509Certificate[] certs, String authType) {
                            }
                        }
                };

                SSLContext sc = SSLContext.getInstance("SSL");
                sc.init(null, trustAllCerts, new java.security.SecureRandom());
                HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

                HostnameVerifier allHostsValid = (hostname, session) -> true;

                HttpsURLConnection connection = (HttpsURLConnection) new URL(getAPI() + "plugin").openConnection();

                HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Authorization", getToken());
                connection.setRequestProperty("User-Agent", "Java client");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setUseCaches(false);
                connection.setAllowUserInteraction(false);
                connection.setConnectTimeout(CONNECT_TIMEOUT);
                connection.setReadTimeout(READ_TIMEOUT);
                connection.connect();

                int responseCode = connection.getResponseCode();

                if (responseCode >= 500) {
                    System.out.println("[Pay2Ply] A API da Pay2Ply encontra-se indisponível no momento.");
                } else if (responseCode == 423) {
                    System.out.println("[Pay2Ply] O pagamento de sua loja encontra-se pendente.");
                } else if (responseCode == 400) {
                    System.out.println("[Pay2Ply] O token do servidor não foi encontrado na API.");
                } else if (responseCode == 401) {
                    System.out.println("[Pay2Ply] O token do servidor não foi encontrado na API.");
                } else if (responseCode == 403) {
                    System.out.println("[Pay2Ply] O IP do servidor não é o mesmo deste servidor, configure-o.");
                } else if (responseCode == 200 || responseCode == 201 || responseCode == 204) {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    connection.disconnect();
                    return stringBuilder.toString();
                }

                connection.disconnect();
            } catch (IOException | NoSuchAlgorithmException | KeyManagementException ex) {
                LOGGER.log(Level.SEVERE, null, ex);
            }
            return null;
        }, executorService);
    }

    public CompletableFuture<Void> update(String username, int id) {
        return CompletableFuture.runAsync(() -> {
            try {
                TrustManager[] trustAllCerts = new TrustManager[]{
                        new X509TrustManager() {
                            public X509Certificate[] getAcceptedIssuers() {
                                return null;
                            }

                            public void checkClientTrusted(X509Certificate[] certs, String authType) {
                            }

                            public void checkServerTrusted(X509Certificate[] certs, String authType) {
                            }
                        }
                };

                // Configurar o SSLContext para usar o TrustManager que ignora as verificações
                SSLContext sc = SSLContext.getInstance("SSL");
                sc.init(null, trustAllCerts, new java.security.SecureRandom());
                HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

                // Ignorar a verificação do hostname
                HostnameVerifier allHostsValid = (hostname, session) -> true;

                HttpsURLConnection connection = (HttpsURLConnection) new URL(getAPI() + "plugin/actived/" + username + "/" + id).openConnection();

                HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Authorization", getToken());
                connection.setRequestProperty("User-Agent", "Java client");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setUseCaches(false);
                connection.setAllowUserInteraction(false);
                connection.setConnectTimeout(CONNECT_TIMEOUT);
                connection.setReadTimeout(READ_TIMEOUT);
                connection.connect();

                int responseCode = connection.getResponseCode();

                if (responseCode >= 500) {
                    System.out.println("[Pay2Ply] A API da Pay2Ply encontra-se indisponível no momento.");
                } else if (responseCode == 423) {
                    System.out.println("[Pay2Ply] O pagamento de sua loja encontra-se pendente.");
                } else if (responseCode == 400) {
                    System.out.println("[Pay2Ply] O token do servidor não foi encontrado na API.");
                } else if (responseCode == 401) {
                    System.out.println("[Pay2Ply] O token do servidor não foi encontrado na API.");
                } else if (responseCode == 403) {
                    System.out.println("[Pay2Ply] O IP do servidor não é o mesmo deste servidor, configure-o.");
                }

                connection.disconnect();
            } catch (IOException | NoSuchAlgorithmException | KeyManagementException ex) {
                LOGGER.log(Level.SEVERE, null, ex);
            }
        }, executorService);
    }
}
