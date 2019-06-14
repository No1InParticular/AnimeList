package com.thomasali.animelist;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class FilterDialog extends Dialog implements android.view.View.OnClickListener {

    public SearchActivity searchActivity;
    public Dialog d;
    public Button yes, no;
    public Spinner status, rated;

    public FilterDialog(SearchActivity searchActivity) {
        super(searchActivity);
        // TODO Auto-generated constructor stub
        this.searchActivity = searchActivity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.filter_dialog);
        yes = (Button) findViewById(R.id.dialog_ok);
        no = (Button) findViewById(R.id.dialog_cancel);
        yes.setOnClickListener(this);
        no.setOnClickListener(this);

        status = findViewById(R.id.sp_status);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),R.array.status_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        status.setAdapter(adapter);

        switch (searchActivity.status) {
            case "Not Specified":
                status.setSelection(0);
                break;
            case "Airing":
                status.setSelection(1);
                break;
            case "Completed":
                status.setSelection(2);
                break;
            case "TBA":
                status.setSelection(3);
                break;
            default:
                status.setSelection(0);
                break;
        }

        rated = findViewById(R.id.sp_rated);
        adapter = ArrayAdapter.createFromResource(getContext(),R.array.rated_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        rated.setAdapter(adapter);

        switch (searchActivity.rated) {
            case "Not Specified":
                rated.setSelection(0);
                break;
            case "G":
                rated.setSelection(1);
                break;
            case "PG":
                rated.setSelection(2);
                break;
            case "PG13":
                rated.setSelection(3);
                break;
            case "R17":
                rated.setSelection(4);
                break;
            case "R":
                rated.setSelection(5);
                break;
            case "RX":
                rated.setSelection(6);
                break;
            default:
                rated.setSelection(0);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialog_ok:
                searchActivity.status = status.getSelectedItem().toString();
                searchActivity.rated = rated.getSelectedItem().toString();
                searchActivity.search();
                break;
            case R.id.dialog_cancel:
                dismiss();
                break;
            default:
                break;
        }
        dismiss();
    }
}
