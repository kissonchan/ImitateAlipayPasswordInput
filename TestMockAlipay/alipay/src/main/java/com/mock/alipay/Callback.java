package com.mock.alipay;

/**
 * Created by chenjiawei on 16/8/26.
 */
public interface Callback {

    void onForgetPassword();

    void onInputCompleted(CharSequence password);

    void onPasswordCorrectly();

    void onCancel();
}
