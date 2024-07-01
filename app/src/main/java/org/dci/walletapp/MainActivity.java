package org.dci.walletapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    TextView welcomeText;
    TextView currentBalance;
    Button addIncomeButton;
    Button addExpenseButton;
    Button historyButton;
    Button profileButton;
    Button categoryManagementButton;
    Button supportButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);



        welcomeText = findViewById(R.id.welcomeText);
        currentBalance = findViewById(R.id.currentBalance);
        addIncomeButton = findViewById(R.id.addIncomeButton);
        addExpenseButton = findViewById(R.id.addExpenseButton);
        historyButton = findViewById(R.id.historyButton);
        profileButton = findViewById(R.id.profileButton);
        categoryManagementButton = findViewById(R.id.categoryManagementButton);
        supportButton = findViewById(R.id.supportButton);
    }
}
