package com.sungfamily;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.sungfamily.events.ChatMessageReceivedEvent;
import com.sungfamily.models.ChatMessage;
import com.sungfamily.models.HttpCourierResponse;
import com.sungfamily.models.User;
import com.sungfamily.rest.RemoteService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity implements ChatMessageAdapter.ChatMessageCallBack {
    private RecyclerView mRecyclerView;
    private Button mButtonSend;
    private EditText mEditTextMessage;
    private ChatMessageAdapter mAdapter;
    private User user;
    private List<ChatMessage> messages = new ArrayList<>();
    private static ProgressDialog dialog;

    public void showProgress() {
        if (dialog == null)
            dialog = new ProgressDialog(this);
        dialog.setTitle("Loading ... Please wait ...");
        dialog.setIndeterminate(false);
        dialog.show();
    }

    public void showProgress(String message) {
        if (dialog == null)
            dialog = new ProgressDialog(this);
        dialog.setTitle(message);
        dialog.setIndeterminate(false);
        dialog.show();
    }

    public void hideProgress() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
            dialog = null;
        }
    }

    private boolean isOnlineChatEstablished;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        setUpToolbar();
        if (getIntent() != null) {
            user = (User) getIntent().getSerializableExtra("user");
        }

        mRecyclerView = findViewById(R.id.recyclerView);
        mButtonSend = findViewById(R.id.btn_send);
        mEditTextMessage = findViewById(R.id.et_message);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        setUpVirtualAssistantChatMessages();
        mAdapter = new ChatMessageAdapter(this, messages, this);
        mRecyclerView.setAdapter(mAdapter);

        mButtonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = mEditTextMessage.getText().toString();
                if (TextUtils.isEmpty(message)) {
                    return;
                }
                sendMessage(user.getFullName() + ": " + message);
                mEditTextMessage.setText("");
            }
        });
    }

    private void setUpVirtualAssistantChatMessages() {
        messages.add(new ChatMessage("Hello, I am your virtual assistant. Please ask me a question or tap one of the popular questions below.", false, false));
        messages.add(new ChatMessage("I'd like to pay my bill", true, false));
        messages.add(new ChatMessage("I'd like a quote to replace my vehicle", true, false));
        messages.add(new ChatMessage("I need to change my address.", true, false));
    }

    private void setUpToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Help Center");
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ChatMessageReceivedEvent e) {
        if (e != null) {
            if (e.getChatMessage() != null) {
                ChatMessage c = e.getChatMessage();
                updateReceivedMessage(c);
            }
        }
    }

    private void sendMessage(String message) {
        if (message.contains("online chat")) {
            mAdapter.add(new ChatMessage("Looking for an agent to connect to you ...", false, false));
            showProgress("Looking for an agent to connect to you ... Please wait ...");
            hideProgressAfterProgress(3, true);
            isOnlineChatEstablished = true;
            return;
        }
        ChatMessage chatMessage = new ChatMessage(message, true, false);
        mAdapter.add(chatMessage);

        RemoteService remoteService = RemoteService.Factory.getInstance(ChatActivity.this);
        remoteService.sendFirebaseMessage(user.getId(), message)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response<HttpCourierResponse>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Response<HttpCourierResponse> response) {
                        if (response.isSuccessful()) {
                            //do nothing...

                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void updateReceivedMessage(ChatMessage message) {
        mAdapter.add(message);
        mRecyclerView.scrollToPosition(mAdapter.getItemCount() - 1);
    }

    @Override
    public void onPayMyBillButtonClicked(int position) {
        mAdapter.add(new ChatMessage("My Bill has been paid ...", false, false));
        showProgress();
        hideProgressAfterProgress(5, false);
    }

    private void hideProgressAfterProgress(int second, final boolean isOnlineChatEstablished) {
        Observable.timer(second, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Long aLong) {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        hideProgress();
                        if (isOnlineChatEstablished) {
                            Intent intent = new Intent(ChatActivity.this, AgentChatActivity.class);
                            intent.putExtra("user", user);
                            startActivity(intent);
                        }
                    }
                });
    }

    @Override
    public void onQuoteButtonClicked(int position) {
        mAdapter.add(new ChatMessage("A Car Quote has been sent to you via your email ...", false, false));
        showProgress();
        hideProgressAfterProgress(3, false);
    }

    @Override
    public void onChangeMyAddressClicked(int position) {
        mAdapter.add(new ChatMessage("Your address has been updated ...", false, false));
        showProgress();
        hideProgressAfterProgress(3, false);
    }
}
