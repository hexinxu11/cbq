package org.cbq.common.http;


import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.cbq.common.http.jdk11.JdkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static org.cbq.common.constant.CbqConstant.CHARSET_UTF8;


/**
 * @author hexinxu
 * @version 2018年05月26日 hexinxu
 * @since 1.0
 */
public class HttpHandle {
    public final static String USER_AGENT_VALUE = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.71 Safari/537.36";

    public final static String USER_AGENT_NAME = "User-Agent";

    public final static String TIMESTAMP = "Timestamp";
    public final static String GET_METHOD = "GET";
    public final static String POST_METHOD = "POST";
    public final static String NEXT_LINE = "\n";
    public final static String HTTPS = "https://";


    static Logger logger = LoggerFactory.getLogger(HttpHandle.class);

    public String handle(String url, String method, Map<String, String> paras, Map<String, String> headers) {
        logger.info("http请求执行，url[{}]、paras[{}]、headers[{}]", url, paras, headers);
        if (method.equals(GET_METHOD)) {
            return execute(get(url, paras, headers));
        } else if (method.equals(POST_METHOD)) {
            return execute(post(url, paras, headers));
        }
        return null;
    }

    /**
     * 文件上传
     *
     * @param url
     * @param fileBodyMap
     * @param headers
     * @return
     */
    public String uploadFile(String url, Map<String, ContentBody> fileBodyMap, Map<String, String> headers) {
        HttpPost post = post(url, null, headers);
        if (fileBodyMap != null) {
            MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
            fileBodyMap.entrySet().stream().forEach(f -> {
                multipartEntityBuilder.addPart(f.getKey(), f.getValue());
            });
            post.setEntity(multipartEntityBuilder.build());
        }
        return execute(post);
    }

    /**
     * 文件下载
     *
     * @param url
     * @param paras
     * @param headers
     * @return
     */
    public CloseableHttpResponse downloadFile(String url, Map<String, String> paras, Map<String, String> headers) {
        CloseableHttpResponse response = null;
        try {
            return executeGetResponse(get(url, paras, headers));
//           HttpEntity entity = response.getEntity();
//           if (entity == null) {
//               return null;
//           }
//           return entity.getContent();
        } catch (Exception e) {
            logger.error("根据url[{}]，下载文件失败，失败原因：", url, e);
        }
        return null;
    }

    private void setHeader(Object object, Map<String, String> headers) {
        if (null == headers) {
            return;
        }
        if (object instanceof HttpPost) {
            headers.entrySet().stream().forEach(f -> {
                ((HttpPost) object).setHeader(f.getKey(), f.getValue());
            });
        } else if (object instanceof HttpGet) {
            headers.entrySet().stream().forEach(f -> {
                ((HttpGet) object).setHeader(f.getKey(), f.getValue());
            });
        }
    }

    private HttpGet get(String url, Map<String, String> paras, Map<String, String> headers) {
        HttpGet get = new HttpGet(constructionUrl(url, paras));
        setHeader(get, headers);
        get.setHeader(USER_AGENT_NAME, USER_AGENT_VALUE);
        return get;
    }

    private HttpPost post(String url, Map<String, String> paras, Map<String, String> headers) {
        HttpPost post = new HttpPost(url);
        post.setHeader(USER_AGENT_NAME, USER_AGENT_VALUE);
        setHeader(post, headers);
        List<NameValuePair> list = Lists.newArrayList();
        try {
            if (paras != null && !paras.isEmpty()) {
                paras.entrySet().stream().forEach(p -> {
                    list.add(new BasicNameValuePair(p.getKey(), p.getValue()));
                });
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list, CHARSET_UTF8);
                post.setEntity(entity);
            }
        } catch (UnsupportedEncodingException e) {
            logger.error("http请求执行失败，失败原因：", e);
        }
        return post;
    }

    public String execute(Object httpMethodObj) {
        CloseableHttpResponse response = null;
        String responseContent = null;
        try {
            response = executeGetResponse(httpMethodObj);
            responseContent = EntityUtils.toString(response.getEntity(), CHARSET_UTF8);
            logger.info("http请求执行执行成功，返回值[{}]", responseContent);
        } catch (IOException e) {
            logger.error("http请求执行失败，失败原因：", e);
        } finally {
            close(null, response);
        }

        return responseContent;
    }

    public CloseableHttpResponse executeGetResponse(Object httpMethodObj) {
        CloseableHttpClient client = HttpClientPool.getConnection();
        CloseableHttpResponse response = null;
        try {
            if (httpMethodObj instanceof HttpGet) {
                response = client.execute((HttpGet) httpMethodObj);
            } else if (httpMethodObj instanceof HttpPost) {
                response = client.execute((HttpPost) httpMethodObj);
            }
        } catch (IOException e) {
            close(client, null);
            logger.error("http请求执行失败，失败原因：", e);
        }
        return response;
    }


    public static void close(CloseableHttpClient client, CloseableHttpResponse response) {
        try {
            if (null != response) {
                response.close();
            }
            if (null != client) {
                client.close();
            }
        } catch (IOException e) {
            logger.error("http连接关闭失败，失败原因：", e);
        }
    }

    public static String constructionUrl(String url, Map<String, String> paras) {
        StringBuffer sbf = new StringBuffer(url);
        if (null != paras) {
            paras.entrySet().stream().forEach(f -> {
                sbf.append("&" + f.getKey() + "=" + f.getValue());
            });
            sbf.replace(sbf.indexOf("&"), sbf.indexOf("&") + 1, "?");
        }
        return sbf.toString();
    }

    static List<Long> countTime = Lists.newArrayList();

    static class HttpClientTest2 implements Runnable {

        String url;

        public HttpClientTest2(String url) {
            this.url = url;
        }

        @Override
        public void run() {
            long startTime = System.currentTimeMillis();

            IntStream.range(0,100).forEach(f->{
                new HttpHandle().handle(url, GET_METHOD, null, null);
            });
            countTime.add((System.currentTimeMillis() - startTime));
        }
    }


    public static void main(String[] args) {
//        Map<String, String> headers = Maps.newHashMap();
//        headers.put(":authority", "ware.shop.jd.com");
//        headers.put(":method", "GET");
//        headers.put(":path", "/rest/shop/fxg/result/query?wareId=12702082605");
//        headers.put(":scheme", "https");
//        headers.put("accept", "application/json, text/plain, */*");
//        headers.put("accept-encoding", "gzip, deflate, br");
//        headers.put("accept-language", "zh-CN,zh;q=0.9");
//        headers.put("cookie","__jdv=95931165|direct|-|none|-|1557213045353; pinId=DU13cGX9no6oNN22PCLvHw; pin=%E6%94%80%E6%B5%B7%E6%B5%B7; unick=%E6%94%80%E6%B5%B7%E6%B5%B7; ceshi3.com=000; _tp=PIxcL6f8lYqmvkj9ku5D5qEqnswI3nmGu5ZKYVSBGCc%3D; _pst=%E6%94%80%E6%B5%B7%E6%B5%B7; __jdu=1798893206; csrf_token=c9ae52db-eb18-4ded-9139-d7359f19782a; language=zh_CN; AESKEY=517CB21121A8259B; QRCodeKEY=061D6A21ABC6484A1A1FE052B12DDCC1FDC54D55B35FE5F2023C2F6DAEFA309A9D2938D1818DA81A6C2501F503C01641; UIDKEY=31817379405328820; thor=61BE545E6DD0E07D2FACD7CF081FA5EE0164272CD0B5679174BB91750F914B9C95EEC181FBC0DD5CFE3FDB89CB8B780AD74A53FC82BE0DAC296C63C93C9037B8589C9D03A77E421F11F8994077B308CA8CAAF0D3858286836BBFF58D084019B1259A1754D6393476A2B001446475DBD39C9E1B35163438FFBE1B017704828A0C; TrackID=1qP5s1azAcNs0pU7N-6yvoikKEQBYjwVURFQ_BpIgE5kLqajlMn7zqPAin6gejRPY; _vender_=TNK3O6PALVQGHGPSFUEVBXUKRVSRXRZY22LZSO7XF3MGVELGVDJ52LLWPCYLIRZAPKZPNLICZAHPWCFPKINP2Q6JVFTLRR2VM7NO33VCW4VJVXN4NB73K4XRE6HFQZT7FB4S5RB67DMEQKZYFEXS26KUJQEQQ4F5HA64IVF2KDFN247BGY47EJP6NQJPXD53F64YA5LB5Y3I33RGP7PDNO2SUVOHDIP7ILUYZBGWFNDV5AX36WPPM467RDJT2HEVM7RU2ERYICZUXCVSHAWUHSKYUTCKXJFXDA4ROXWORBW5XWUDPSLNJNNWYCTL5WS4HSZZCSHUOBS27ODFUJPXET3LCU7FW7QHGD63LTRE4QCGQPJGBZFBEMGNQ5OW3EMHQBOKNWAX5ARVU22QG3EBBW7J7FMDQQGDQPPHZSHV7E5JFEVI4ZLUVUBDRRQ7Z2O2CXOQFHHO3RMCZFIKE3IDZQT6M7MXOWO6IRMRYJSP7T52EWYSQGLKBPW3ERVJWRI4CIYM3B25NWIYOU6I5NJVXJF4T4; b-sec=7MSSLXU4ZFWJ362IWMDYDJSKJXNASLFUYGXPWJ2KNRTWQGVWDBSA; _base_=YKH2KDFHMOZBLCUV7NSRBWQUJPBI7JIMU5R3EFJ5UDHJ5LCU7R2NILKK5UJ6GLA2RGYT464UKXAI5KK7PNC5B5UHJ3AGFI6LZAJ26QWCERXU47Z3A5C2VDJOVZOORH4KDNCWLEAN4TOBZ4FILV63M4BURY63MVL2O2ZTOI7TURXWO464AQYXFGQNOET6APSTM2ND634QOTZ6JTPOUTOPTOVIB3IDQQVJTBSNYQTAMGK3KWXUC7X45CEHAEWV75FZGPD6NKUSB5G5C4TP7CXDNX7L4ULSG3HR6PS2UQXKU6P4IZPKZA4DYNECY5YKAJYNIKDBY3PDM25KDK5TQNMPS5JN7E; _BELONG_CLIENT_=WPSC4XJXWK5USS4JNZY2X7VRLR5MCBKRSVHEXABGTHDGISIQK5YOLZUXYE7IOIM7MOKO74H6CRN6WHAAR4TMDV3XZWMXZRCRT5XRNE3V356BTOB2Y7LPK66VWQK6HPTGWVXIDXDCPVE3W5WMHAIO6AT2LX2XXVNUCXR34ZWFK6HY45CORGIKOSYDYZBF27WOKTUX6BS4FZMIJWNUX6CB4JAA25ZLF7ZEKYOO4QV5HTSBXGNRM3E242MBI6V5D4C5VJDQ3EOYCOW5BMTUJZACIBHXQGQKC7QDZ7SWJ5WXO4A7GOL46ZZ5ZDJMRZQ6TVNVODKXYKDBGMOJBY4HOUZQTN73YJNOUS5JUIPWQODVC2GET5RXIL3Q; _vender_new_=GI63BGTJFDBQ4MVCVZUWRLO226CYMZWM5BV43ACPNX6LYGATEQED25EXTBLCENPUB2XLBKR5RIO2A2CTRLTDMEDRB5BQZSUDENBWBWYTARI6GCFOQY3EWD2CEHHEQSH5F5JQJ4GWGC3NJM2GV7M34WQGHOU6PKETSSVDPNULM3NIPN5O5CIQNUKJ2ADW4RGTCFYJMMGC4UHQL4XSTPFJ6PVA2PLN3UCVJFGIT5RYMXAWOY2XWQCXI6RB3XVDWHR6HDOD2SNRC5NX4DRRYOYE7UGG7DZYIVI7Y64CIII6EIFNKPQKP2RXERMDYTSLRPIZTIMGFR4OHOZ7HBC2QYY46IA63QNTFAD4EIRFFDGBR3INTZ4FKHFJ3YT5J2L3PXJ3DMZIA7BCEJJIZQMO2DM6PBKRZIF6LW5NZEFZK5NO627VUSSP5BZYXV5DEZZGSFF6OIQDZKME7A4WZC3ZCVCJNC6FXJWD6YYUE3LXEPQ346CACIL4NDRMSCZ33V3DSPLYZIZQIDJIEJT5SCESKHVQDASCV5TFNUJCU3JIY4PZDDVGWNWQIZX6WRLRQSLYZWDLUOE4H5B42SG5ODTUTCRZ4JWN5BJVBQR3GD2FEYBEME5BJ6CWEDN3TY6HUOI4AK6RF7HTZ52PKYPZX5OLRLGVQWLB6QPIVTGH5RV5RFQMTZXV4E2AVWTQ; 3AB9D23F7A4B3C9B=6URTNDOHR65GDAYH6QXNWSMZBDXZUR6FDPJBZKMJP3SPL7QPS4BWVCNBV22IM6UMXM6HGECCCJPWYIZ43APNRJT76Y; JSESSIONID=B22955983073C6A4E38BE8788281C7E8.s1; __jda=191954476.1798893206.1552569307.1557234508.1557392407.10; __jdc=191954476; __jdb=191954476.7.1798893206|10.1557392407");
//        headers.put("referer","https://ware.shop.jd.com/rest/shop/fxg/query");
//        headers.put("user-agent","Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/73.0.3683.103 Safari/537.36");
//        headers.put("x-requested-with","XMLHttpRequest");
//
//        new HttpHandle().handle("https://www.mzitu.com/tag/youhuo/",GET_METHOD,null,headers);
        String url = "https://zhidao.baidu.com/question/2208745763613974508.html";
        IntStream.range(0, 100).forEach(f -> {

            new Thread(new HttpClientTest2(url + f)).start();
        });
        while (countTime.size() != 100) {
            try {
                Thread.sleep(1000);
                System.out.println(countTime.stream().count());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
        System.out.println("-----------------------");
        countTime.stream().forEach(System.out::println);
        System.out.println(countTime.stream().mapToLong(x -> x).sum());
    }
}


