package com.example.consultamedicaapp;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

public class CpfMask implements TextWatcher {
    private EditText editText;

    public CpfMask(EditText editText) {
        this.editText = editText;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        // Não precisa fazer nada aqui
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        // Não precisa fazer nada aqui
    }

    @Override
    public void afterTextChanged(Editable s) {
        String cpf = s.toString().replaceAll("[^\\d]", "");
        StringBuilder formattedCpf = new StringBuilder();

        if (cpf.length() > 0) {
            formattedCpf.append(cpf.substring(0, Math.min(cpf.length(), 3)));
        }
        if (cpf.length() > 3) {
            formattedCpf.append(".").append(cpf.substring(3, Math.min(cpf.length(), 6)));
        }
        if (cpf.length() > 6) {
            formattedCpf.append(".").append(cpf.substring(6, Math.min(cpf.length(), 9)));
        }
        if (cpf.length() > 9) {
            formattedCpf.append("-").append(cpf.substring(9, Math.min(cpf.length(), 11)));
        }

        // Atualiza o EditText com a máscara aplicada
        editText.removeTextChangedListener(this);
        editText.setText(formattedCpf.toString());
        editText.setSelection(formattedCpf.length());
        editText.addTextChangedListener(this);
    }
}
