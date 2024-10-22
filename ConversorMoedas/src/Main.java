import com.google.gson.Gson;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;


public class Main {
    private static final String API_URL = "https://v6.exchangerate-api.com/v6/72b6713571aa7ec99c5cec5a/latest/";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Bem-vindo ao conversor de moedas!");


        String[] moedas = {"USD", "EUR", "GBP", "JPY", "AUD", "CAD"};
        while (true) {
            System.out.println("Escolha uma moeda de origem:");
            for (int i = 0; i < moedas.length; i++) {
                System.out.println((i + 1) + ". " + moedas[i]);
            }
            System.out.println("0. Sair");

            int escolha = scanner.nextInt();
            if (escolha == 0) {
                System.out.println("Saindo do programa...");
                break;
            }

            if (escolha < 1 || escolha > moedas.length) {
                System.out.println("Opção inválida. Tente novamente.");
                continue;
            }

            String moedaOrigem = moedas[escolha - 1];
            System.out.println("Você escolheu: " + moedaOrigem);


            System.out.print("Digite o valor que deseja converter: ");
            double valor = scanner.nextDouble();

            System.out.println("Escolha uma moeda de destino:");
            for (int i = 0; i < moedas.length; i++) {
                System.out.println((i + 1) + ". " + moedas[i]);
            }

            escolha = scanner.nextInt();
            if (escolha < 1 || escolha > moedas.length) {
                System.out.println("Opção inválida. Tente novamente.");
                continue;
            }

            String moedaDestino = moedas[escolha - 1];
            System.out.println("Convertendo de " + moedaOrigem + " para " + moedaDestino + "...");


            double taxaConversao = obterTaxaDeCambio(moedaOrigem, moedaDestino);
            if (taxaConversao != -1) {
                double valorConvertido = valor * taxaConversao;
                System.out.printf("Valor convertido: %.2f %s\n", valorConvertido, moedaDestino);
            } else {
                System.out.println("Erro ao obter a taxa de câmbio.");
            }
        }
        scanner.close();
    }


    private static double obterTaxaDeCambio(String moedaOrigem, String moedaDestino) {
        try {
            URL url = new URL(API_URL + moedaOrigem);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();


            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                throw new RuntimeException("Erro HTTP: " + responseCode);
            }


            InputStreamReader reader = new InputStreamReader(conn.getInputStream());
            Gson gson = new Gson();
            ExchangeRateResponse response = gson.fromJson(reader, ExchangeRateResponse.class);


            if (!"success".equals(response.result)) {
                throw new RuntimeException("Erro na resposta da API.");
            }


            return response.conversion_rates.get(moedaDestino);
        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
            return -1;
        }
    }
}
