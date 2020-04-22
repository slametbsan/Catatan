package id.ac.aknganjuk.catatan;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private CatatanAdapter catatanAdapter;
    private List<Catatan> catatanList = new ArrayList<>();
    private LinearLayout viewCatatanKosong;
    private CatatanDbHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogCatatan(false, null, -1);
            }
        });

        RecyclerView recyclerView = findViewById(R.id.rvCatatan);
        viewCatatanKosong = findViewById(R.id.catatanKosong);

        db = new CatatanDbHelper(this);

        catatanList.addAll(db.getSemuaCatatan());

        catatanAdapter = new CatatanAdapter(this, catatanList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(catatanAdapter);

        toggleCatatanKosong();

        recyclerView.addOnItemTouchListener(new CatatanTouchListener(this,
                recyclerView, new CatatanTouchListener.ClickListener() {
            @Override
            public void onLongClick(View view, int position) {
                showDialogAksi(position);
            }

            @Override
            public void onClick(View view, int position) { }
        }));
    }

    private void showDialogAksi(final int position) {
        CharSequence[] opsi = new CharSequence[]{"Edit", "Hapus"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pilih aksi");
        builder.setItems(opsi, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which == 0){
                    showDialogCatatan(true, catatanList.get(position), position);
                } else {
                    deleteCatatan(position);
                }
            }
        });
        builder.show();
    }

    private void showDialogCatatan(final boolean akanDiupdate, final Catatan catatan, final int position) {
        LayoutInflater layoutInflater = LayoutInflater.from(getApplicationContext());
        View view = layoutInflater.inflate(R.layout.catatan_dialog, null);

        AlertDialog.Builder dialogInputCatatan = new AlertDialog.Builder(MainActivity.this);
        dialogInputCatatan.setView(view);

        final EditText etCatatan = view.findViewById(R.id.etCatatan);
        TextView tvJudulDialog = view.findViewById(R.id.tvJudulDialog);
        tvJudulDialog.setText(!akanDiupdate ? getString(R.string.catatanBaruLabel)
                : getString(R.string.catatanEditLabel));
        if(akanDiupdate && catatan != null){
            etCatatan.setText(catatan.getCatatan());
        }

        dialogInputCatatan
                .setCancelable(false)
                .setPositiveButton(akanDiupdate ? "perbarui" : "simpan",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                .setNegativeButton("batal", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        final AlertDialog alertDialog = dialogInputCatatan.create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLUE);
                alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED);
            }
        });
        alertDialog.show();

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //tampilkan toast jika tombol di-klik saat belum ada catatan
                if(TextUtils.isEmpty(etCatatan.getText().toString())){
                    Toast.makeText(MainActivity.this, "Tulis catatan dulu.", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    alertDialog.dismiss();
                }

                //cek jika user sedang update catatan
                if(akanDiupdate && catatan != null){
                    updateCatatan(etCatatan.getText().toString(), position);
                } else {
                    createCatatan(etCatatan.getText().toString());
                }
            }
        });
    }

    private void createCatatan(String txtCatatan) {
        long id = db.insertCatatan(txtCatatan);
        Catatan ctt = db.getCatatan(id);
        if(ctt != null){
            catatanList.add(0, ctt);
            catatanAdapter.notifyDataSetChanged();
            toggleCatatanKosong();
        }
    }

    private void updateCatatan(String txtCatatan, int position) {
        Catatan ctt = catatanList.get(position);
        ctt.setCatatan(txtCatatan);
        db.updateCatatan(ctt);
        catatanList.set(position, ctt);
        catatanAdapter.notifyItemChanged(position);
        toggleCatatanKosong();
    }

    private void deleteCatatan(int position) {
        db.deleteCatatan(catatanList.get(position));
        catatanList.remove(position);
        catatanAdapter.notifyItemRemoved(position);
        toggleCatatanKosong();
    }

    private void toggleCatatanKosong() {
        if(db.getJumlahCatatan() > 0){
            viewCatatanKosong.setVisibility(View.GONE);
        } else {
            viewCatatanKosong.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menuAbout) {
            showDialogAbout();
        }

        return true;
    }

    private void showDialogAbout() {
        LayoutInflater layoutInflater = LayoutInflater.from(getApplicationContext());
        View view = layoutInflater.inflate(R.layout.catatan_about, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setView(view);

        String str = "Icons made by <a href=\"https://www.flaticon.com/authors/dinosoftlabs\" title=\"DinosoftLabs\">DinosoftLabs</a> from <a href=\"https://www.flaticon.com/\" title=\"Flaticon\"> www.flaticon.com</a>";
        TextView text = view.findViewById(R.id.tvCopyright);
        text.setText(Html.fromHtml(str));
        builder.show();
    }
}
