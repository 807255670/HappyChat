package cn.monitor4all.happychat.service;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipaySystemOauthTokenRequest;
import com.alipay.api.response.AlipaySystemOauthTokenResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

/**
 * Created By Fan Huiliang
 * 2019-07-14 14:53
 */
@Service
public class AlipayLoginService implements InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(AlipayLoginService.class);

    /**Alipay客户端*/
    private AlipayClient alipayClient;

    /**支付宝网关*/
    private static final String ALIPAY_BORDER_DEV = "https://openapi.alipaydev.com/gateway.do";
    private static final String ALIPAY_BORDER_PROD = "https://openapi.alipay.com/gateway.do";
    /**appID**/
    private static final String APP_ID_DEV = "xxxxxx";
    private static final String APP_ID_PROD = "2019071465849264";
    /**私钥*/
    private static final String APP_PRIVATE_KEY = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCunbRU0q1Ol3ZV" +
            "B3qHqSOlA+25IXgAcZid6ppEMqsOYcTkehPXkUokGJLCvEox6xp6/U+Tb3UDGMcZ" +
            "V5rLagloNwbwQzA/NJ+juBvzGiw1IORN+sYGgW8LeNWSsz09IHYBz7myHfddMTYf" +
            "JjFSsCfLa/+OCSwYDmX/KMxX6qFLlwWmXJb5FUp90mimtpxzOffXkyoMJrB2p34K" +
            "gpQ5LnXkh7kBHHeigP6dj0p2TrUtdtdnXxgYV3nOzdAKcPkJbVRSv19MQor/NlPO" +
            "4XrK5wGTCUj+n+M5PBn2A7euzzeOCbulMIQKLYZFqJjZuuu9UimwMnqnKAbGwirR" +
            "KBVj6hl3AgMBAAECggEAKPJCP0HH8g4CaZ3/VaCMAz4QmMEIatI4f49r7WIUSqMS" +
            "kKEv1fTz9dLtV0zf+SSwaPbNmj0gq0E8Lu0kF18eJZoBvoWC4s9sP/AEXdVdWHgG" +
            "EWMk3JGtgHstWeE+Ev9xrKsRWHs+blQD5PoHxuQX8NO6cYwlEt8Ne/ANOvCCMj8s" +
            "TK1XtlN+3h0qlhmoEhvWKyfW3bOTN7bi/paC54dRfgJ5yxpfpujEO9iJ2riJTVja" +
            "B53nwmRAiJWCrRR7LvYrawGYfSWYGgIObmfZkDAciAX9qzX9zCwOIrx+puPNL3KZ" +
            "ySDT1Dvmn0mzbH7kYnrBLgCCwGgwYCNgudNm4W/aQQKBgQDlpZiX4ilwiNHDqkzA" +
            "uWLEM1HQldrxAXjNY7KklvtHv7pgTJaCgy9wy5IXexUaT2U7Ox0rMyDDpxfO5rIW" +
            "apida3MK8uH55hd2nw0Nw+s1sgLeiCbm5UTqsurgH+SezoLEkShK10lhsOJEWSSm" +
            "gAh1RpF7sO741NPKQX3rwKD+9QKBgQDCp3OQr5ab9ma2hbzphc14BdZwKG3ZH3Vo" +
            "dvIG2qqzc8L0h/GWwAcj7o+xy1fsD3lMvWINaCU0isI13nZioTUGTykW73beIZ0O" +
            "wNl9jScHT/3s6HxM6qf8xnOSVoFAEtIa/1Uq2vEFnF2KGrwaTjoNSXvvnwTvbBJY" +
            "FDGMCg2bOwKBgCBYndCu42VdcCZeQ2dV9WprTBfUM72Elo5ff5cdSRGHGOpdf0ms" +
            "zq7VjaNPC97j1f9sySP/lJraW0kiR21O4GYz70VYhqnbVey4ZG9O7D9o7myHwur8" +
            "2iMwxlKBS4Nu1Q5437MC9ewTrRsxby8gk5BpOy6v8kkMOTSg2uqs/EBNAoGBAIYQ" +
            "8wWKCj9tHB1GS3DDWVr7GlCxZ8cNZb4Hfna+EKf8aSAmqXxc0g4rdwjLtsi9d5wl" +
            "K+1z93wSveSATiVsRqsLxo8na0Ve/V9179YLbAAbVhbfoQ0Q6Zat0FfQAMFytQcn" +
            "KS+VED//DGXcOAYeLtmL7TKC4dFNK+1CK0X6fpiVAoGBAIL1HQ+VLW/GriiiHkfK" +
            "hImaHAAInJRAO82VUlZ32Hmr3eR+3r03BXwcg5pM2ZiE6jzYkly3QKm+8kuYT47p" +
            "5cIyNyoXAkfuGocNR0Vd5xwapj4hC2mIi64tLEMQSUZEcyRyTmERJTBQBk67Qn1t" +
            "VR1w3UAbNtNFedkL0+CfI4mi";
    /**公钥*/
    private static final String ALIPAY_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAjKgpo04TLEqgYjETnMQC5LfT4jn0gKCk9eq2ho3ernH07Mxif47tILEE+584Ln2fMvC0+O4Rs55yVyh7UgwvKS046y9iT5Gn2JLsqoAxEoqbonilOgDGrMIRixtx4fg8SzuMw9USylMsA9cyqF0BIHp8XkB882u+XU/4aPZTTGp2eLyXxQQoUF6woMFYvTWnz7JRcpWRQ3c8mC+g9O/t2Vko0o/qpt67t3LClHlGi/INVUdAyBvpPatzhe9DYhxR/s65HF0IyZ5FTmNKf+0gY4xKM1B65YsYpG7Rs7ZtneE0Jnw2KBksTxtZ/Ykj4sH7MO6JigBvcjvs+hsNHIo3KwIDAQAB";

    @Override
    public void afterPropertiesSet() throws Exception {
        alipayClient = new DefaultAlipayClient(ALIPAY_BORDER_PROD, APP_ID_PROD, APP_PRIVATE_KEY, "json", "UTF-8", ALIPAY_PUBLIC_KEY, "RSA2");
    }

    /**
     * 根据auth_code获取用户的user_id和access_token
     * @param authCode
     * @return
     */
    public String getUserid(String authCode) {
        AlipaySystemOauthTokenRequest request = new AlipaySystemOauthTokenRequest();
        request.setCode(authCode);
        request.setGrantType("authorization_code");
        try {
            AlipaySystemOauthTokenResponse oauthTokenResponse = alipayClient.execute(request);
            LOGGER.info("支付宝userid  "+oauthTokenResponse.getUserId());
            return oauthTokenResponse.getUserId();
        } catch (Exception e) {
            LOGGER.error("使用authCode获取信息失败！", e);
            return null;
        }
    }


}
