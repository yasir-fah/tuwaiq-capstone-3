package com.fkhrayef.capstone3.Service;


import com.fkhrayef.capstone3.Api.ApiException;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.RequestBody;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class WhatsappService {

    @Value("${whatsapp.ultramsg.key}")
    private String whatsappKey;

    public void sendTextMessage(String message, String phoneNumber) throws ApiException {

        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("token", whatsappKey)
                .add("to", phoneNumber)
                .add("body", message)
                .build();

        Request request = new Request.Builder()
                .url("https://api.ultramsg.com/instance139636/messages/chat")
                .post(body)
                .addHeader("content-type", "application/x-www-form-urlencoded")
                .build();

        try {
            Response response = client.newCall(request).execute();
            System.out.println(response.body().string());
        }catch (Exception e){
            throw new ApiException("Error while sending message to Whatsapp");
        }

    }
}
