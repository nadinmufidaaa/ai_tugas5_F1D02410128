import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Test {

    static int jumlahGagal = 0;

    static void uji(String nama, List<String> faktaYa, List<String> diharapkan) {
        Main.Hasil h = Main.forwardChaining(faktaYa);

        List<String> akhir = Main.cariKesimpulanAkhir();
        List<String> didapat = new ArrayList<>();
        for (String fakta : h.memori) {
            if (akhir.contains(fakta)) {
                didapat.add(fakta);
            }
        }

        Collections.sort(didapat);
        List<String> urutDiharapkan = new ArrayList<>(diharapkan);
        Collections.sort(urutDiharapkan);

        if (didapat.equals(urutDiharapkan)) {
            System.out.println("LULUS " + nama);
        } else {
            System.out.println("GAGAL " + nama + " -> " + didapat);
            jumlahGagal++;
        }
    }

    public static void main(String[] args) throws Exception {
        Main.bacaFile("aturan_banjir.txt");

        uji("1. Hujan ekstrem, tas siaga belum ada, saluran tersumbat",
            Arrays.asList("hujan di atas 150 mm per hari", "tas siaga belum disiapkan", "saluran air tersumbat"),
            Arrays.asList("SIAPKAN TAS SIAGA SEKARANG", "BERSIHKAN SALURAN AIR"));

        uji("2. Negatif: hanya ada anggota keluarga rentan",
            Arrays.asList("ada anggota keluarga rentan"),
            new ArrayList<String>());

        uji("3. Siaga 2, ada anggota rentan, harus lewat jalan banjir",
            Arrays.asList("pintu air siaga 2", "ada anggota keluarga rentan", "harus lewat jalan banjir naik kendaraan"),
            Arrays.asList("EVAKUASI KE TEMPAT AMAN DAN MATIKAN LISTRIK",
                          "EVAKUASI KELOMPOK RENTAN LEBIH DULU",
                          "JANGAN BERKENDARA DI WILAYAH BANJIR"));

        uji("4. Siaga 3 dan rumah di bantaran sungai",
            Arrays.asList("pintu air siaga 3", "rumah di bantaran sungai"),
            Arrays.asList("SIAPKAN RUTE EVAKUASI DAN TITIK KUMPUL"));

        uji("5. Air banjir sudah surut",
            Arrays.asList("air banjir sudah surut"),
            Arrays.asList("WASPADA SETELAH BANJIR: LISTRIK, BINATANG BERBISA, JALAN AMBLES"));

        System.out.println("Gagal: " + jumlahGagal);
    }
}
