package com.cisco.acvpj.kat_handler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.FileNotFoundException;
import java.io.FileReader;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.cisco.acvpj.exceptions.ACVPJUnsupportedOperationException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class AESKATHandlerTest {

    @Test
    public void testHandlerNullJson() {
        AESKATHandler handler = new AESKATHandler();
        JsonObject json = new JsonObject();

        assertThrows(ACVPJUnsupportedOperationException.class,
                () -> handler.handler(json));
    }

    @Test
    public void testHandlerNullAlgorithm() {
        AESKATHandler handler = new AESKATHandler();
        JsonObject json = new JsonObject();
        json.addProperty("vsId", 0);

        assertThrows(ACVPJUnsupportedOperationException.class,
                () -> handler.handler(json));
    }

    @Test
    public void testHandlerInvalidAlgorithm() {
        AESKATHandler handler = new AESKATHandler();
        JsonObject json = new JsonObject();
        json.addProperty("vsId", 0);
        json.addProperty("algorithm", "SHA-512");

        assertThrows(ACVPJUnsupportedOperationException.class,
                () -> handler.handler(json));
    }

    @Test
    public void testHandlerAFT() {
        AESKATHandler handler = new AESKATHandler();
        String inputJson = "{" +
                "  \"vsId\" : 3670," +
                "  \"algorithm\" : \"AES-CBC\"," +
                "  \"testGroups\" : [ {" +
                "    \"direction\" : \"encrypt\"," +
                "    \"testType\" : \"AFT\"," +
                "    \"keyLen\" : 128," +
                "    \"tests\" : [ {" +
                "      \"tcId\" : 1," +
                "      \"iv\" : \"FE70BA3D7F5B7A3E6760730A890692BB\"," +
                "      \"key\" : \"751859ABBB36ADD266AD24F73A5AC97A\"," +
                "      \"pt\" : \"ACBD74839EC4AE969D4246C8308A25F5\"" +
                "    }, {" +
                "      \"tcId\" : 2," +
                "      \"iv\" : \"C2499681E8E906C3E2AB57BAB28FFD97\"," +
                "      \"key\" : \"5EE30E6049A3BD8634E4730BAD892E71\"," +
                "      \"pt\" : \"2F0717CF4C03D2AD48A00FEB542DC63EDC60C3EA4EF123C4826B8E5218FF8AEF\"" +
                "    }, {" +
                "      \"tcId\" : 3," +
                "      \"iv\" : \"F0F91FE38660AF73AFA0AB2E4BF754F1\"," +
                "      \"key\" : \"ACB31E8B806AB7E9E0728FE19A0BC75D\"," +
                "      \"pt\" : \"30BA8CFE967E24A5B08E5DDFC0CB3C53A5562AEC02C9AE8B074E6C0C08EF408816C6B825E5904A1BE622AD34ECB7C11C\"" +
                "    }]}]}";
        String expected = "{" +
                "\"vsId\": 3670," +
                "\"algorithm\": \"AES-CBC\"," +
                "\"testResults\": [" +
                "{" +
                "\"tcId\": 1," +
                "\"ct\": \"5D05B756094E196AA1255FBC12E960AC\"" +
                "}," +
                "{" +
                "\"tcId\": 2," +
                "\"ct\": \"955C3D85A4DE63667BA6EF47CDD184E3680C1D45E7991190F5439ABFC14DB38A\"" +
                "}," +
                "{" +
                "\"tcId\": 3," +
                "\"ct\": \"8F6CFB389374C63AD72DD5B35FACFF71BD3206C23A66E084027A2276EF298D7377E6FA400B1E9FB97EA2436D6BAFC9C1\"" +
                "}]}";

        JsonParser parser = new JsonParser();

        JsonObject actual = handler.handler(parser.parse(inputJson).getAsJsonObject());
        assertEquals(parser.parse(expected), actual);
    }

    @Test
    public void testHandlerMCTEncrypt() {
        AESKATHandler handler = new AESKATHandler();
        String inputJson = "" +
                "{" +
                "  \"vsId\" : 470," +
                "  \"algorithm\" : \"AES-CBC\"," +
                "  \"testGroups\" : [" +
                "  {" +
                "    \"direction\" : \"encrypt\"," +
                "    \"testType\" : \"MCT\"," +
                "    \"keyLen\" : 128," +
                "    \"tests\" : [ {" +
                "      \"tcId\" : 589," +
                "      \"iv\" : \"903F3317CCAA1C076ACB4D4BA7E4A439\"," +
                "      \"key\" : \"9AE46803F7E5FF8D65BB71BDDCA9F94E\"," +
                "      \"pt\" : \"84C7E1604187829CC2C6F32BD693806D\"" +
                "    } ]" +
                "  } ]" +
                "}";

        JsonObject expected = new JsonObject();
        JsonParser parser = new JsonParser();

        try {
            expected = parser.parse(new FileReader("test\\files\\AESKATHandlerTestMCTEncryptExpectedOutput.json")).getAsJsonObject();
        } catch (FileNotFoundException e) {
            Assertions.fail(e);
        }

        JsonObject actual = handler.handler(parser.parse(inputJson).getAsJsonObject());
        assertEquals(expected, actual);
    }

    @Test
    public void testHandlerMCTDecrypt() {
        AESKATHandler handler = new AESKATHandler();
        String inputJson = "" +
                "{" +
                "  \"vsId\" : 470," +
                "  \"algorithm\" : \"AES-CBC\"," +
                "  \"testGroups\" : [" +
                "{" +
                "    \"direction\" : \"decrypt\"," +
                "    \"testType\" : \"MCT\"," +
                "    \"keyLen\" : 128," +
                "    \"tests\" : [ {" +
                "      \"tcId\" : 590," +
                "      \"iv\" : \"9F67D4684C1A7EE9C7580D6DF73F9ACE\"," +
                "      \"key\" : \"AB0579E93A40E7325A6468A0965E0F37\"," +
                "      \"ct\" : \"C7597AD9ACEDCB28602DED2968286037\"" +
                "    } ]" +
                "} ] }";

        JsonObject expected = new JsonObject();
        JsonParser parser = new JsonParser();

        try {
            expected = parser.parse(new FileReader("test\\files\\AESKATHandlerTestMCTDecryptExpectedOutput.json")).getAsJsonObject();
        } catch (FileNotFoundException e) {
            Assertions.fail(e);
        }

        JsonObject actual = handler.handler(parser.parse(inputJson).getAsJsonObject());
        assertEquals(expected, actual);
    }
}
