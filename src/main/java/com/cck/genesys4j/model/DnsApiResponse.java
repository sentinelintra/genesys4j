package com.cck.genesys4j.model;

import com.genesyslab.platform.applicationblocks.com.ConfigException;
import com.genesyslab.platform.applicationblocks.com.objects.CfgDN;

import java.util.Collection;

public class DnsApiResponse extends BaseApiResponse {

    private final Collection<CfgDN> dns;

    protected DnsApiResponse(Collection<CfgDN> dns, Integer errorCode, String errorMessage) {
        super(errorCode, errorMessage);
        this.dns = dns;
    }

    public static DnsApiResponse success(Collection<CfgDN> dns) {
        return new DnsApiResponse(dns, Errors.SUCCESS, ApiResponse.SUCCESS_MESSAGE);
    }

    public static DnsApiResponse failure(Integer errorCode, String errorMessage) {
        return new DnsApiResponse(null, errorCode, errorMessage);
    }

    public static DnsApiResponse configException(ConfigException e) {
        return failure(ApiResponse.Errors.CFG_ERROR,
                "Unable to request on config server, error: " + e.getMessage());
    }

    public static DnsApiResponse interruptedException(InterruptedException e) {
        return failure(ApiResponse.Errors.INTERRUPTED_ERROR,
                "Retrieving multiple objects process was interrupted, error: " + e.getMessage());
    }

    public Collection<CfgDN> getDns() {
        return dns;
    }
}
