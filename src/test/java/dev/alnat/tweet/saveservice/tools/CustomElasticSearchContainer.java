package dev.alnat.tweet.saveservice.tools;

import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.utility.DockerImageName;

/**
 * Created by @author AlNat on 27.07.2023
 * Licensed by Apache License, Version 2.0
 */
public class CustomElasticSearchContainer extends ElasticsearchContainer {

    private static final String ELASTIC_SEARCH_DOCKER = "docker.elastic.co/elasticsearch/elasticsearch:8.9.0";
    private static final String CLUSTER_NAME = "cluster.name";
    private static final String ELASTIC_SEARCH = "elasticsearch";
    private static final String DISCOVERY_TYPE = "discovery.type";
    private static final String DISCOVERY_TYPE_SINGLE_NODE = "single-node";
    private static final String XPACK_SECURITY_ENABLED = "xpack.security.enabled";
    private static final String XPACK_SECURITY_TRANSPORT_ENABLED = "xpack.security.transport.ssl.enabled";
    private static final String XPACK_SECURITY_HTTP_SSL_ENABLED = "xpack.security.http.ssl.enabled";

    public CustomElasticSearchContainer() {
        super(DockerImageName.parse(ELASTIC_SEARCH_DOCKER).asCompatibleSubstituteFor("docker.elastic.co/elasticsearch/elasticsearch"));
        withExposedPorts(9200, 9300);
        addEnv(DISCOVERY_TYPE, DISCOVERY_TYPE_SINGLE_NODE);
        addEnv(XPACK_SECURITY_ENABLED, Boolean.FALSE.toString());
        addEnv(XPACK_SECURITY_TRANSPORT_ENABLED, Boolean.FALSE.toString());
        addEnv(XPACK_SECURITY_HTTP_SSL_ENABLED, Boolean.FALSE.toString());
        addEnv(CLUSTER_NAME, ELASTIC_SEARCH);
    }

}
