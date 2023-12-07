package br.com.renato.TabelaFipe.principal;

import br.com.renato.TabelaFipe.model.Dados;
import br.com.renato.TabelaFipe.model.Modelos;
import br.com.renato.TabelaFipe.model.Veiculo;
import br.com.renato.TabelaFipe.service.ConsumoAPI;
import br.com.renato.TabelaFipe.service.ConverteDados;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Principal {
    private Scanner sc = new Scanner(System.in);
    private ConsumoAPI consumoAPI = new ConsumoAPI();
    private ConverteDados converteDados = new ConverteDados();

    private final String URL_BASE = "https://parallelum.com.br/fipe/api/v1/";

    public void exibeMenu(){
        String op = "";
        List<Veiculo> veiculos = new ArrayList<>();

        while (!op.equalsIgnoreCase("sair")) {

            String mensagem = """
                    ***************
                    Escolha o tipo de veículo: 
                                              
                    Carro
                    Moto
                    Caminhão
                    
                    Ou "Sair" para finalizar a aplicação!                          
                    \nDigite sua opção:  
                    """;
            System.out.println(mensagem);
            op = sc.nextLine();

            String url;
            if (op.toLowerCase().contains("carr")) {
                url = URL_BASE + "carros/marcas";
            } else if (op.toLowerCase().contains("mot")) {
                url = URL_BASE + "motos/marcas";
            } else if (op.toLowerCase().contains("caminh")){
                url = URL_BASE + "caminhoes/marcas";
            } else {
                System.out.println("Fim da aplicação!");
                break;
            }

            String json = consumoAPI.obterDados(url);
            System.out.println(json);

            var marcas = converteDados.obterLista(json, Dados.class);
            marcas.stream()
                    .sorted(Comparator.comparing(Dados::codigo))
                    .forEach(System.out::println);

            System.out.println("Informe o código da marca: ");
            String codMarca = sc.nextLine();

            url = url + "/" + codMarca + "/modelos";
            json = consumoAPI.obterDados(url);

            var modeloLista = converteDados.obterDados(json, Modelos.class);

            System.out.println("\nModelos dessa marca: " + url);
            modeloLista.modelos().stream()
                    .sorted(Comparator.comparing(Dados::codigo))
                    .forEach(System.out::println);

            System.out.println("Digite o nome do veículo: ");
            String nomeVeiculo = sc.nextLine();

            List<Dados> modelosFiltrados = modeloLista.modelos().stream()
                    .filter(m -> m.nome().toLowerCase().contains(nomeVeiculo.toLowerCase()))
                    .collect(Collectors.toList());

            System.out.println("\nModelos filtrados: ");
            modelosFiltrados.forEach(System.out::println);

            System.out.println("Digite o código do modelo: ");
            String codModelo = sc.nextLine();

            url = url + "/" + codModelo + "/anos";

            json = consumoAPI.obterDados(url);

            List<Dados> anos = converteDados.obterLista(json, Dados.class);

            for (int i = 0; i < anos.size(); i++) {
                var urlAnos = url + "/" + anos.get(i).codigo();
                json = consumoAPI.obterDados(urlAnos);
                Veiculo veiculo = converteDados.obterDados(json, Veiculo.class);
                veiculos.add(veiculo);
            }

            System.out.println("\nVeículos listados por enquanto:");
            veiculos.forEach(System.out::println);
        }

        System.out.println("\n*************************");
        System.out.println("\nLista de veículos final: ");
        veiculos.stream()
                .sorted(Comparator.comparing(Veiculo::ano))
                .forEach(System.out::println);
        System.out.println("\n*************************");


    }
}
