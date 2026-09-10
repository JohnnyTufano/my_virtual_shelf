package com.example.my_virtual_shelf;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.my_virtual_shelf.adapter.BookAdapter;
import com.example.my_virtual_shelf.api.OpenLibraryResponse;
import com.example.my_virtual_shelf.api.RetrofitClient;
import com.example.my_virtual_shelf.db.AppDatabase;
import com.example.my_virtual_shelf.db.Book;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements BookAdapter.OnBookClickListener {

    private RecyclerView recyclerView;
    private BookAdapter adapter;
    private AppDatabase db;
    private SearchView searchView;

    private final String[] statusOptions = {"Da leggere", "In lettura", "Completato"};

    private final ActivityResultLauncher<Intent> scanLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    String isbn = result.getData().getStringExtra("SCAN_RESULT");
                    if (isbn != null) {
                        fetchBookDetails(isbn);
                    }
                }
            }
    );

    private final ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            isGranted -> {
                if (isGranted) {
                    openScanner();
                } else {
                    Toast.makeText(this, "Permesso fotocamera necessario!", Toast.LENGTH_SHORT).show();
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = AppDatabase.getInstance(this);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));

        adapter = new BookAdapter(new ArrayList<>(), this);
        recyclerView.setAdapter(adapter);

        // Configura la Barra di Ricerca
        searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.filter(newText);
                return false;
            }
        });

        FloatingActionButton fabScan = findViewById(R.id.fabScan);
        fabScan.setOnClickListener(v -> checkPermissionAndScan());

        loadBooks();
    }

    private void checkPermissionAndScan() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            openScanner();
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    private void openScanner() {
        Intent intent = new Intent(this, ScanActivity.class);
        scanLauncher.launch(intent);
    }

    private void fetchBookDetails(String isbn) {
        String key = "ISBN:" + isbn;
        RetrofitClient.getService().getBookInfo(key, "json", "data").enqueue(new Callback<Map<String, OpenLibraryResponse>>() {
            @Override
            public void onResponse(Call<Map<String, OpenLibraryResponse>> call, Response<Map<String, OpenLibraryResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().containsKey(key)) {
                    OpenLibraryResponse bookData = response.body().get(key);
                    String title = bookData.title != null ? bookData.title : "Senza Titolo";
                    String author = bookData.getAuthorsString();
                    String coverUrl = "https://covers.openlibrary.org/b/isbn/" + isbn + "-M.jpg";

                    showAddBookDialog(isbn, title, author, coverUrl);
                } else {
                    showAddBookDialog(isbn, "", "", "");
                }
            }

            @Override
            public void onFailure(Call<Map<String, OpenLibraryResponse>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Errore durante il recupero dei dati", Toast.LENGTH_SHORT).show();
                showAddBookDialog(isbn, "", "", "");
            }
        });
    }

    private void showAddBookDialog(String isbn, String defaultTitle, String defaultAuthor, String coverUrl) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Conferma Libro (ISBN: " + isbn + ")");

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_book, null);
        EditText editTitle = view.findViewById(R.id.editTitle);
        EditText editAuthor = view.findViewById(R.id.editAuthor);
        ImageView imgCover = view.findViewById(R.id.dialogImgCover);
        Spinner spinnerStatus = view.findViewById(R.id.spinnerStatus);

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, statusOptions);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(spinnerAdapter);

        editTitle.setText(defaultTitle);
        editAuthor.setText(defaultAuthor);

        if (!coverUrl.isEmpty()) {
            Glide.with(this).load(coverUrl).into(imgCover);
        }

        builder.setView(view);
        builder.setPositiveButton("Salva", (dialog, which) -> {
            String title = editTitle.getText().toString().trim();
            String author = editAuthor.getText().toString().trim();
            String status = spinnerStatus.getSelectedItem().toString();

            Book newBook = new Book(isbn, title, author, coverUrl, status);
            db.bookDao().insert(newBook);
            loadBooks();
            Toast.makeText(this, "Libro Salvato!", Toast.LENGTH_SHORT).show();
        });

        builder.setNegativeButton("Annulla", null);
        builder.show();
    }

    private void loadBooks() {
        List<Book> books = db.bookDao().getAllBooks();
        adapter.setBooks(books);
    }

    // Gestione del Click Breve (Cambia Stato di Lettura rapidamente)
    @Override
    public void onBookClick(Book book) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Cambia Stato Lettura");

        builder.setSingleChoiceItems(statusOptions, getStatusIndex(book.status), (dialog, which) -> {
            book.status = statusOptions[which];
            db.bookDao().update(book);
            loadBooks();
            dialog.dismiss();
            Toast.makeText(this, "Stato aggiornato!", Toast.LENGTH_SHORT).show();
        });

        builder.setNegativeButton("Annulla", null);
        builder.show();
    }

    // Gestione della Pressione Prolungata (Menu Elimina)
    @Override
    public void onBookLongClick(Book book) {
        new AlertDialog.Builder(this)
                .setTitle("Elimina Libro")
                .setMessage("Sei sicuro di voler eliminare \"" + book.title + "\" dalla tua libreria?")
                .setPositiveButton("Elimina", (dialog, which) -> {
                    db.bookDao().delete(book);
                    loadBooks();
                    Toast.makeText(this, "Libro eliminato", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Annulla", null)
                .show();
    }

    private int getStatusIndex(String status) {
        if (status == null) return 0;
        for (int i = 0; i < statusOptions.length; i++) {
            if (statusOptions[i].equalsIgnoreCase(status)) return i;
        }
        return 0;
    }
}