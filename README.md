# Tugas 5 - Sistem Pakar Sederhana: Kesiapsiagaan Banjir

- **Nama:** Nadin Mufida
- **NIM:** F1D02410128
- **Mata kuliah:** Kecerdasan Buatan 

## 1. Topik dan cara menjalankan

Topik: **kesiapsiagaan banjir untuk keluarga** (bukan kesehatan manusia). Program bertanya kondisi hujan, status siaga sungai, rumah, dan keluarga, lalu menyimpulkan langkah yang perlu dilakukan beserta alasannya.

- Bahasa: Java (JDK 21), tanpa library tambahan. _(Ganti angka versi sesuai hasil `java -version` di laptop Anda.)_
- Mesin forward chaining ditulis sendiri di `Main.java`. Aturan ada di file teks `aturan_banjir.txt`.

Jalankan dari dalam folder ini:

```bash
javac Main.java Test.java   # compile
java Main                   # jalankan program (jawab y/t)
java Test                   # uji otomatis 5 kasus
```

File: `aturan_banjir.txt` (aturan dan sumber), `Main.java` (program), `Test.java` (uji), `hasil/` (keluaran teks), `screenshot/` (tangkapan layar).

Format file aturan: satu baris satu data, dipisah tanda `|`. Contoh `ATURAN|R4|dan|ada potensi banjir;tas siaga belum disiapkan|SIAPKAN TAS SIAGA SEKARANG|S1,S3` artinya aturan R4, mode dan, dua syarat (dipisah `;`), hasil, dan sumber.

## 2. Sumber

| Kode | Judul | Penerbit | URL | Diakses |
|---|---|---|---|---|
| S1 | Panduan Kesiapsiagaan Menghadapi Banjir bagi Masyarakat (2020; kata pengantar bertanggal September 2020, Cetakan Pertama Oktober 2020) | BPBD Provinsi DKI Jakarta | https://ppid.jakarta.go.id/assets/pdf/buku-saku-panduan-kesiapsiagaan-menghadapi-banjir-bagi-masyarakat.pdf | 11 Nov 2026 |
| S2 | Cuaca Ekstrem Masih Mengintai, BMKG Ingatkan Kesiapsiagaan Nasional (siaran pers, 12 Sep 2025) | BMKG | https://www.bmkg.go.id/siaran-pers/cuaca-ekstrem-masih-mengintai-bmkg-ingatkan-kesiapsiagaan-nasional | 10 Nov 2026 |
| S3 | Banjir (hlm. 32-39), Buku Saku Tanggap Tangkas Tangguh Menghadapi Bencana (tahun tidak tercantum di berkas) | BNPB | https://ppid.kkp.go.id/media/uploads/document_information_public/4._Siaga_Bencana_Banjir.pdf (salinan di PPID KKP) | 10 Nov 2026 |

Aturan adalah parafrasa, bukan kutipan. Isi yang diambil dari sumber:
- S1: status siaga pintu air (Siaga 4 Normal, Siaga 3 Waspada, Siaga 2 Siaga, Siaga 1 Awas); jika ada potensi banjir, siapkan tas siaga dan matikan listrik; saat banjir, evakuasi dan utamakan kelompok rentan; jangan mengemudi di wilayah banjir; jangan abaikan saluran air tersumbat; setelah banjir, waspada listrik, binatang berbisa, dan area ambles.
- S2: hujan di atas 150 mm per hari termasuk ekstrem; drainase tersumbat sampah dan sedimen memperparah genangan.
- S3: hujan lebat terus-menerus penyebab banjir; tahu tempat tinggal di zona rawan, siapkan rute evakuasi dan tas siaga untuk 3 hari; matikan listrik, jangan mengemudi di banjir; setelah banjir hindari air banjir dan area yang baru surut.

Cara menggabungkan beberapa fakta menjadi satu kesimpulan (misalnya R4 dan R5) adalah rumusan saya sendiri.

## 3. Tabel aturan (10 aturan, 7 kesimpulan akhir)

Hasil antara (miring) dipakai aturan lain. Kesimpulan akhir ditulis tebal.

| Aturan | IF | THEN | Sumber |
|---|---|---|---|
| R1 | hujan di atas 150 mm per hari | *hujan ekstrem* | S2 |
| R2 | *hujan ekstrem* ATAU hujan lebat terus-menerus ATAU pintu air siaga 3 | *ada potensi banjir* | S1, S2, S3 |
| R3 | pintu air siaga 2 ATAU pintu air siaga 1 | *banjir mengancam* | S1 |
| R4 | *ada potensi banjir* DAN tas siaga belum disiapkan | **SIAPKAN TAS SIAGA SEKARANG** | S1, S3 |
| R5 | *ada potensi banjir* DAN rumah di bantaran sungai | **SIAPKAN RUTE EVAKUASI DAN TITIK KUMPUL** | S1, S3 |
| R6 | *banjir mengancam* | **EVAKUASI KE TEMPAT AMAN DAN MATIKAN LISTRIK** | S1, S3 |
| R7 | *banjir mengancam* DAN ada anggota keluarga rentan | **EVAKUASI KELOMPOK RENTAN LEBIH DULU** | S1 |
| R8 | saluran air tersumbat | **BERSIHKAN SALURAN AIR** | S1, S2 |
| R9 | harus lewat jalan banjir naik kendaraan | **JANGAN BERKENDARA DI WILAYAH BANJIR** | S1, S3 |
| R10 | air banjir sudah surut | **WASPADA SETELAH BANJIR: LISTRIK, BINATANG BERBISA, JALAN AMBLES** | S1, S3 |

Rantai aturan: R1 -> R2 -> R4 (hasil R1 dipakai R2, hasil R2 dipakai R4). Fakta yang ditanyakan ke user adalah fakta yang tidak pernah menjadi hasil aturan (11 pertanyaan).

## 4. Metode: Forward Chaining

Dipilih karena user sudah tahu faktanya (hujan, status siaga, kondisi rumah), dan kesimpulan muncul dari rantai aturan. Langkahnya:

```
memori <- fakta yang dijawab "ya"
ULANGI:
    cocok <- aturan yang syaratnya terpenuhi DAN hasilnya belum ada di memori
    JIKA cocok kosong: BERHENTI
    r <- aturan pertama di cocok
    tambahkan r.maka ke memori dan catat bahwa r.maka berasal dari r
```

Mode `dan` = semua syarat harus ada di memori. Mode `atau` = salah satu syarat cukup. Aturan yang hasilnya sudah ada tidak dijalankan lagi supaya program berhenti. Alasan ditampilkan dengan menelusuri balik dari kesimpulan ke aturan yang menghasilkannya, sampai ke fakta jawaban user. Setelah satu diagnosa selesai, program bertanya "Diagnosa lagi?" sehingga bisa dipakai berulang.

## 5. Kasus uji

| # | Jawaban "ya" | Kesimpulan | Keluaran |
|---|---|---|---|
| 1 | hujan di atas 150 mm, tas siaga belum disiapkan, saluran tersumbat | siapkan tas siaga; bersihkan saluran air | `hasil/kasus1.txt` |
| 2 (negatif) | ada anggota keluarga rentan saja | tidak ada | `hasil/kasus2.txt` |
| 3 | pintu air siaga 2, ada anggota rentan, harus lewat jalan banjir naik kendaraan | evakuasi dan matikan listrik; evakuasi kelompok rentan lebih dulu; jangan berkendara | `hasil/kasus3.txt` |

`Test.java` juga menguji 2 kasus lain (siaga 3 + bantaran sungai; air sudah surut). Kasus 2 tidak menghasilkan kesimpulan karena anggota rentan baru relevan kalau banjir mengancam (R7 butuh R3).

![Kasus 1](screenshot/kasus1.png)
![Kasus 2 (tidak ada kesimpulan)](screenshot/kasus2.png)
![Kasus 3](screenshot/kasus3.png)

## 6. Batas keberlakuan

Pengetahuan dalam program ini berasal dari tiga dokumen resmi: panduan BPBD Provinsi DKI Jakarta (2020), siaran pers BMKG (12 September 2025), dan bab Banjir dari buku saku BNPB. Aturannya hanya untuk langkah kesiapsiagaan tingkat keluarga, bukan untuk memprediksi banjir. Status siaga pintu air mengacu pada sistem di Jakarta; di daerah lain gunakan status siaga dari BPBD setempat. Angka 150 mm per hari adalah batas hujan ekstrem menurut BMKG, bukan batas banjir di semua tempat. Hasilnya adalah saran awal, bukan keputusan akhir; selalu ikuti peringatan BMKG dan arahan petugas BPBD serta RT/RW setempat. Catatan ini juga ditampilkan di akhir keluaran program.

## 7. Penggunaan alat bantu AI

Alat bantu AI (Claude) dipakai untuk menyusun draf aturan dari sumber, menulis kode awal program.
