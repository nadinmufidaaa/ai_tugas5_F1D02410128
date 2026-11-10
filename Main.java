import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Main {
    static class Aturan {
        String id;
        String mode;                        
        List<String> jika = new ArrayList<>();   
        String maka;                          
        String sumber;                        
    }

    static class Hasil {
        List<String> memori = new ArrayList<>();           
        Map<String, Aturan> asal = new LinkedHashMap<>();   
        List<Aturan> urutan = new ArrayList<>();            
    }

    static List<Aturan> daftarAturan = new ArrayList<>();
    static Map<String, String> pertanyaan = new LinkedHashMap<>();
    static String judul = "";
    static String batas = "";

    static void bacaFile(String namaFile) throws Exception {
        daftarAturan.clear();
        pertanyaan.clear();

        List<String> semuaBaris = Files.readAllLines(Paths.get(namaFile), StandardCharsets.UTF_8);
        for (String baris : semuaBaris) {
            baris = baris.trim();
            if (baris.isEmpty() || baris.startsWith("#")) {
                continue;   
            }
            String[] bagian = baris.split("\\|");

            if (bagian[0].equals("JUDUL")) {
                judul = bagian[1];
            } else if (bagian[0].equals("BATAS")) {
                batas = bagian[1];
            } else if (bagian[0].equals("TANYA")) {
                pertanyaan.put(bagian[1], bagian[2]);
            } else if (bagian[0].equals("ATURAN")) {
                Aturan a = new Aturan();
                a.id = bagian[1];
                a.mode = bagian[2];
                for (String syarat : bagian[3].split(";")) {
                    a.jika.add(syarat.trim());
                }
                a.maka = bagian[4];
                a.sumber = bagian[5];
                daftarAturan.add(a);
            }
        }
    }

    static List<String> cariFaktaDasar() {
        List<String> faktaDasar = new ArrayList<>();
        for (Aturan a : daftarAturan) {
            for (String fakta : a.jika) {
                boolean adalahHasil = false;
                for (Aturan b : daftarAturan) {
                    if (b.maka.equals(fakta)) {
                        adalahHasil = true;
                    }
                }
                if (!adalahHasil && !faktaDasar.contains(fakta)) {
                    faktaDasar.add(fakta);
                }
            }
        }
        return faktaDasar;
    }

    static List<String> cariKesimpulanAkhir() {
        List<String> akhir = new ArrayList<>();
        for (Aturan a : daftarAturan) {
            boolean dipakaiLagi = false;
            for (Aturan b : daftarAturan) {
                if (b.jika.contains(a.maka)) {
                    dipakaiLagi = true;
                }
            }
            if (!dipakaiLagi) {
                akhir.add(a.maka);
            }
        }
        return akhir;
    }

    static boolean syaratTerpenuhi(Aturan a, List<String> memori) {
        int jumlahAda = 0;
        for (String syarat : a.jika) {
            if (memori.contains(syarat)) {
                jumlahAda++;
            }
        }
        if (a.mode.equals("dan")) {
            return jumlahAda == a.jika.size();
        } else {
            return jumlahAda >= 1;
        }
    }

    static Hasil forwardChaining(List<String> faktaAwal) {
        Hasil h = new Hasil();
        h.memori.addAll(faktaAwal);

        while (true) {
            Aturan dipilih = null;
            for (Aturan a : daftarAturan) {
                if (syaratTerpenuhi(a, h.memori) && !h.memori.contains(a.maka)) {
                    dipilih = a;    
                    break;
                }
            }

            if (dipilih == null) {
                break;              
            }

            h.memori.add(dipilih.maka);
            h.asal.put(dipilih.maka, dipilih);
            h.urutan.add(dipilih);
        }
        return h;
    }

    static void cetakAlasan(String fakta, Hasil h, int tingkat) {
        Aturan a = h.asal.get(fakta);
        if (a == null) {
            return;     
        }

        List<String> syaratDipakai = new ArrayList<>();
        for (String syarat : a.jika) {
            if (h.memori.contains(syarat)) {
                syaratDipakai.add(syarat);
            }
        }

        String spasi = "  ".repeat(tingkat);
        System.out.println(spasi + fakta + "  <- " + a.id
                + " (" + String.join(", ", syaratDipakai) + ") [" + a.sumber + "]");

        for (String syarat : syaratDipakai) {
            cetakAlasan(syarat, h, tingkat + 1);
        }
    }

    static boolean bacaYaTidak(Scanner in, String teks) {
        while (true) {
            System.out.print(teks);
            if (!in.hasNextLine()) {
                return false;      
            }
            String jawab = in.nextLine().trim().toLowerCase();
            if (jawab.equals("y")) {
                return true;
            }
            if (jawab.equals("t")) {
                return false;
            }
            System.out.println("   Ketik y atau t.");
        }
    }

    public static void main(String[] args) throws Exception {
        bacaFile("aturan_banjir.txt");
        Scanner in = new Scanner(System.in);
        List<String> faktaDasar = cariFaktaDasar();
        List<String> kesimpulanAkhir = cariKesimpulanAkhir();

        boolean ulangi = true;
        while (ulangi) {
            System.out.println(judul);
            System.out.println("Jawab y/t.");

            List<String> faktaAwal = new ArrayList<>();
            int nomor = 1;
            for (String fakta : faktaDasar) {
                boolean ya = bacaYaTidak(in, nomor + ". " + pertanyaan.get(fakta) + " (y/t) > ");
                if (ya) {
                    faktaAwal.add(fakta);
                }
                nomor++;
            }

            System.out.println();
            if (faktaAwal.isEmpty()) {
                System.out.println("Fakta awal: (tidak ada)");
            } else {
                System.out.println("Fakta awal: " + String.join(", ", faktaAwal));
            }
            System.out.println();

            Hasil h = forwardChaining(faktaAwal);

            int siklus = 1;
            for (Aturan a : h.urutan) {
                System.out.println("Siklus " + siklus + ": " + a.id + " -> " + a.maka);
                siklus++;
            }
            System.out.println("Siklus " + siklus + ": tidak ada aturan baru, berhenti");
            System.out.println();

            List<String> kesimpulan = new ArrayList<>();
            for (Aturan a : h.urutan) {
                if (kesimpulanAkhir.contains(a.maka)) {
                    kesimpulan.add(a.maka);
                }
            }

            if (kesimpulan.isEmpty()) {
                System.out.println("Kesimpulan: tidak ada kesimpulan dari fakta tersebut.");
            } else {
                System.out.println("Kesimpulan:");
                for (String k : kesimpulan) {
                    System.out.println("  - " + k);
                }
                System.out.println("Alasan:");
                for (String k : kesimpulan) {
                    cetakAlasan(k, h, 1);
                }
            }

            System.out.println();
            System.out.println("Catatan: " + batas);
            System.out.println();

            ulangi = bacaYaTidak(in, "Diagnosa lagi? (y/t) > ");
            System.out.println();
        }
    }
}
