import json
import textwrap

def baca_kb(nama_file):
    with open(nama_file, encoding="utf-8") as f:
        return json.load(f)


def cari_fakta_dasar(kb):
    semua_hasil = []
    for aturan in kb["aturan"]:
        semua_hasil.append(aturan["maka"])

    fakta_dasar = []
    for aturan in kb["aturan"]:
        for fakta in aturan["jika"]:
            if fakta not in semua_hasil and fakta not in fakta_dasar:
                fakta_dasar.append(fakta)
    return fakta_dasar


def cari_kesimpulan_akhir(kb):
    semua_syarat = []
    for aturan in kb["aturan"]:
        semua_syarat = semua_syarat + aturan["jika"]

    akhir = []
    for aturan in kb["aturan"]:
        if aturan["maka"] not in semua_syarat:
            akhir.append(aturan["maka"])
    return akhir


def syarat_terpenuhi(aturan, memori):
    jumlah_ada = 0
    for syarat in aturan["jika"]:
        if syarat in memori:
            jumlah_ada = jumlah_ada + 1

    if aturan["mode"] == "dan":
        return jumlah_ada == len(aturan["jika"])
    else:
        return jumlah_ada >= 1


def forward_chaining(kb, fakta_awal):
    memori = list(fakta_awal)  
    asal = {}                  
    urutan = []                 

    while True:
        cocok = []
        for aturan in kb["aturan"]:
            if syarat_terpenuhi(aturan, memori) and aturan["maka"] not in memori:
                cocok.append(aturan)

        if len(cocok) == 0:     
            break

        aturan = cocok[0]     
        memori.append(aturan["maka"])
        asal[aturan["maka"]] = aturan
        urutan.append(aturan)

    return memori, asal, urutan


def cetak_alasan(fakta, asal, memori, tingkat):
    if fakta not in asal:
        return      

    aturan = asal[fakta]

    syarat_dipakai = []
    for syarat in aturan["jika"]:
        if syarat in memori:
            syarat_dipakai.append(syarat)

    print("  " * tingkat + fakta + "  <- " + aturan["id"]
          + " (" + ", ".join(syarat_dipakai) + ") [" + ", ".join(aturan["sumber"]) + "]")

    for syarat in syarat_dipakai:
        cetak_alasan(syarat, asal, memori, tingkat + 1)


def tanya_ya_tidak(teks):
    while True:
        jawab = input(teks).strip().lower()
        if jawab == "y":
            return True
        if jawab == "t":
            return False
        print("   Ketik y atau t.")


def satu_diagnosa(kb, fakta_dasar, kesimpulan_akhir):
    print(kb["judul"])
    print("Jawab y/t.")

    fakta_awal = []
    nomor = 1
    for fakta in fakta_dasar:
        if tanya_ya_tidak(str(nomor) + ". " + kb["pertanyaan"][fakta] + " (y/t) > "):
            fakta_awal.append(fakta)
        nomor = nomor + 1

    print()
    if len(fakta_awal) == 0:
        print("Fakta awal: (tidak ada)")
    else:
        print("Fakta awal: " + ", ".join(fakta_awal))
    print()

    memori, asal, urutan = forward_chaining(kb, fakta_awal)

    siklus = 1
    for aturan in urutan:
        print("Siklus " + str(siklus) + ": " + aturan["id"] + " -> " + aturan["maka"])
        siklus = siklus + 1
    print("Siklus " + str(siklus) + ": tidak ada aturan baru, berhenti")
    print()

    kesimpulan = []
    for aturan in urutan:
        if aturan["maka"] in kesimpulan_akhir:
            kesimpulan.append(aturan["maka"])

    if len(kesimpulan) == 0:
        print("Kesimpulan: tidak ada kesimpulan dari fakta tersebut.")
    else:
        print("Kesimpulan:")
        for k in kesimpulan:
            print("  - " + k)
        print("Alasan:")
        for k in kesimpulan:
            cetak_alasan(k, asal, memori, 1)

    print()
    print(textwrap.fill("Catatan: " + kb["batas"], width=80))
    print()


def main():
    kb = baca_kb("kb_banjir.json")
    fakta_dasar = cari_fakta_dasar(kb)
    kesimpulan_akhir = cari_kesimpulan_akhir(kb)

    ulangi = True
    while ulangi:
        satu_diagnosa(kb, fakta_dasar, kesimpulan_akhir)
        ulangi = tanya_ya_tidak("Diagnosa lagi? (y/t) > ")
        print()


if __name__ == "__main__":
    main()
