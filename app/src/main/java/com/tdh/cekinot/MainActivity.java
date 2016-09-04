package com.tdh.cekinot;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    final Context context = this;
    public String[] player_names = new String[4];
    public int turns=0;
    public int[] total_score= new int[4];
    Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submit_score();
                fab.animate().rotationBy(720).translationYBy(-600).translationXBy(-450).scaleX(7).scaleY(7);
                fab.hide();
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        this.menu = menu;
        // call init method
        init();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_log) {
            Toast.makeText(MainActivity.this,R.string.toast_incomplete,Toast.LENGTH_SHORT).show();
        }else if(id == R.id.action_reset){
            LayoutInflater li = LayoutInflater.from(context);
            final View promptV = li.inflate(R.layout.prompt_reset, null);

            // init AlertDialog Builder
            final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
            dialogBuilder
                    .setView(promptV)
                    .setCancelable(false)
                    .setPositiveButton(R.string.prompt_ok,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    reset_score();
                                    Toast.makeText(MainActivity.this,R.string.toast_reseted,Toast.LENGTH_SHORT ).show();
                                }
                            })
                    .setNegativeButton(R.string.prompt_cancel,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });

            final AlertDialog alertDialog = dialogBuilder.create();
            alertDialog.show();


        }
        return super.onOptionsItemSelected(item);
    }

    private void submit_score(){
        LayoutInflater li = LayoutInflater.from(context);
        final View promptV = li.inflate(R.layout.prompt_score, null);

        TextView[] tv = new TextView[4];
        final EditText[] et = new EditText[4];
        final int[] score_in = new int[4];
        for(int x=0;x<4;x++){
            String a = "prompt_n"+String.valueOf(x+1);
            String b = "prompt_s"+String.valueOf(x+1);
            int tvid = getResources().getIdentifier(a,"id",this.getPackageName());
            int etid = getResources().getIdentifier(b,"id",this.getPackageName());
            tv[x] = (TextView) promptV.findViewById(tvid);
            tv[x].setText(player_names[x]);
            et[x] = (EditText) promptV.findViewById(etid);
        }

        // init AlertDialog Builder
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        dialogBuilder
                .setView(promptV)
                .setCancelable(false)
                .setPositiveButton(R.string.prompt_ok,null)
                .setNegativeButton(R.string.prompt_cancel,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

        final AlertDialog alertDialog = dialogBuilder.create();

        alertDialog.setOnShowListener(new DialogInterface.OnShowListener(){
            @Override
            public void onShow(DialogInterface dialog) {
                Button b = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                b.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        int x;
                        for(x=0;x<4;x++){
                            score_in[x]=et[x].getText().toString().length()!=0?Integer.parseInt(et[x].getText().toString()):1;
                            Log.d("test",String.valueOf(score_in[x]));
                            if(score_in[x]%5!=0){
                                et[x].requestFocus();
                                Toast.makeText(MainActivity.this, R.string.toast_input_invalid, Toast.LENGTH_SHORT).show();
                                break;
                            }
                        }
                        if(x==4){
                            save_score(score_in);
                            alertDialog.dismiss();
                        }
                    }
                });
            }
        });
        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener(){
            @Override
            public void onDismiss(DialogInterface dialog) {
                FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
                fab.show();
                fab.animate().rotationBy(-720).translationY(0).translationX(0).scaleY(1).scaleX(1).setDuration(800);
            }
        });

        alertDialog.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        alertDialog.show();
    }

    public void change_name(View v){
        final TextView t = (TextView) v;
        final String prev_name = t.getText().toString();
        final String id = getResources().getResourceEntryName(t.getId());
        String sub = id.substring(6);
        final int idx = Integer.parseInt(sub)-1;
        Log.d("test",id+" was pressed");

        // init Layout Inflater & View layout
        LayoutInflater li = LayoutInflater.from(context);
        View promptV = li.inflate(R.layout.prompt_name, null);

        // init AlertDialog Builder
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        dialogBuilder.setView(promptV);

        final EditText nameInput = (EditText) promptV.findViewById(R.id.prompt_name_edit);
        nameInput.setText(prev_name);

        dialogBuilder
                .setCancelable(false)
                .setPositiveButton(R.string.prompt_ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (nameInput.getText().length() != 0) {
                                    String name = nameInput.getText().toString();
                                    t.setText(name);
                                    SharedPreferences sp_name = getSharedPreferences("com.tdh.cekinot.playerName", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor sp_name_editor = sp_name.edit();
                                    sp_name_editor.putString(id, name);
                                    sp_name_editor.apply();
                                    player_names[idx]=name;
                                }
                            }
                        })
                .setNegativeButton(R.string.prompt_cancel,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        alertDialog.show();
    }

    private void save_score(int[] score_in){
        SharedPreferences sp_total = getSharedPreferences("com.tdh.cekinot.totalScore", Context.MODE_PRIVATE);
        SharedPreferences.Editor sp_total_editor = sp_total.edit();
        for(int x=0;x<4;x++){
            total_score[x]+=score_in[x];
            sp_total_editor.putInt("p"+String.valueOf(x+1),total_score[x]);

        }
        sp_total_editor.putInt("turns",++turns);
        sp_total_editor.apply();
        write_score(total_score, turns);
    }

    private void write_score(int[] score, int t){
        // Name Text View Array
        TextView[] tview_score=new TextView[4];
        TextView tview_turn=(TextView) findViewById(R.id.total_match);
        tview_turn.setText(String.valueOf(t)+"x permainan");
        for(int x=0; x<4; x++){
            String id_score = "score"+String.valueOf(x+1);
            int res_score = getResources().getIdentifier(id_score, "id",this.getPackageName());
            tview_score[x] = (TextView) findViewById(res_score);
            tview_score[x].setText(String.valueOf(score[x]));
        }
        if(turns==1){
            MenuItem i = menu.getItem(1);
            i.setVisible(true);
        }
    }

    private void reset_score(){
        SharedPreferences prefs = getSharedPreferences("com.tdh.cekinot.totalScore",Context.MODE_PRIVATE);
        SharedPreferences.Editor e = prefs.edit();
        e.clear().apply();
        init();
    }

    private void init(){

        // Name Text View Array
        TextView[] tview_name=new TextView[4];
        TextView[] tview_score=new TextView[4];
        TextView tview_turn=(TextView) findViewById(R.id.total_match);

        for(int x=0; x<4; x++){
            String id_name = "name_p"+String.valueOf(x+1);
            String id_score = "score"+String.valueOf(x+1);
            int res_name = getResources().getIdentifier(id_name, "id",this.getPackageName());
            int res_score = getResources().getIdentifier(id_score, "id",this.getPackageName());
            tview_name[x] = (TextView) findViewById(res_name);
            tview_score[x] = (TextView) findViewById(res_score);
        }

        // shared preferences name
        SharedPreferences sp_name = getSharedPreferences("com.tdh.cekinot.playerName", Context.MODE_PRIVATE);
        SharedPreferences sp_total = getSharedPreferences("com.tdh.cekinot.totalScore", Context.MODE_PRIVATE);

        player_names = getResources().getStringArray(R.array.player_name);
        int score;
        for(int x=0;x<4;x++){
            player_names[x] = sp_name.getString("name_p"+String.valueOf(x+1) ,player_names[x]);
            score = sp_total.getInt("p" + String.valueOf(x + 1), 0);
            total_score[x]=score;
            tview_name[x].setText(player_names[x]);
            tview_score[x].setText(String.valueOf(score));
        }

        turns = sp_total.getInt("turns", 0);
        String matches = turns==0?"": String.valueOf(turns)+"x permainan";
        tview_turn.setText(matches);

        if(turns==0){
            MenuItem i = menu.getItem(1);
            i.setVisible(false);
        }

    }
}