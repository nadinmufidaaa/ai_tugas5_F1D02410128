from main import baca_kb, forward_chaining, cari_kesimpulan_akhir

kb = baca_kb("kb_banjir.json")
akhir = cari_kesimpulan_akhir(kb)

kasus = [
    ("1. Hujan ekstrem, tas siaga belum ada, saluran tersumbat",
     ["hujan di atas 150 mm per hari", "tas siaga belum disiapkan", "saluran air tersumbat"],
     ["SIAPKAN TAS SIAGA SEKARANG",
      "PANTAU INFORMASI RESMI DAN AMANKAN BARANG BERHARGA KE TEMPAT TINGGI",
      "BERSIHKAN SALURAN AIR"]),

    ("2. Negatif: hanya ada anggota keluarga rentan",
     ["ada anggota keluarga rentan"],
     []),

    ("3. Siaga 2, ada anggota rentan, harus lewat jalan banjir",
     ["pintu air siaga 2", "ada anggota keluarga rentan", "harus lewat jalan banjir naik kendaraan"],
     ["EVAKUASI KE TEMPAT AMAN DAN MATIKAN LISTRIK",
      "EVAKUASI KELOMPOK RENTAN LEBIH DULU",
      "JANGAN BERKENDARA DI WILAYAH BANJIR"]),

    ("4. Siaga 3 saja (waspada)",
     ["pintu air siaga 3"],
     ["PANTAU INFORMASI RESMI DAN AMANKAN BARANG BERHARGA KE TEMPAT TINGGI"]),

    ("5. Air banjir sudah surut",
     ["air banjir sudah surut"],
     ["WASPADA SETELAH BANJIR: LISTRIK, BINATANG BERBISA, JALAN AMBLES"]),
]

jumlah_gagal = 0
for nama, fakta_ya, diharapkan in kasus:
    memori, asal, urutan = forward_chaining(kb, fakta_ya)

    didapat = []
    for fakta in memori:
        if fakta in akhir:
            didapat.append(fakta)

    if sorted(didapat) == sorted(diharapkan):
        print("LULUS", nama)
    else:
        print("GAGAL", nama, "->", didapat)
        jumlah_gagal = jumlah_gagal + 1

print("Gagal:", jumlah_gagal)
