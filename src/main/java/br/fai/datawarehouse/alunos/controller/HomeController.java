package br.fai.datawarehouse.alunos.controller;

import br.fai.datawarehouse.alunos.helper.NamesHelper;
import br.fai.datawarehouse.alunos.model.Alunos;
import br.fai.datawarehouse.alunos.service.AlunosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class HomeController {

    @Autowired
    private AlunosService service;

    private List<Alunos> filterAlunos = null;

    private List<String> etniasList = null;
    private List<String> sexoList = null;
    private List<String> escolaList = null;

    private String etniaSelecionado = "";
    private String sexoSelecionado = "";
    private String escolaSelecionado = "";

    private String selectedPage = "";

    private String subTitle = "";


    private boolean notFoundAlunos;

    private void setOptionsFilter(){
        List<Alunos> alunosList = service.getAllAlunos();
        etniasList = alunosList.stream()
                .map(Alunos::getEtnia).distinct().collect(Collectors.toList());

        sexoList = alunosList.stream()
                .map(Alunos::getSexo).distinct().collect(Collectors.toList());

        escolaList = alunosList.stream()
                .map(Alunos::getEscola_origem).distinct().collect(Collectors.toList());
    }

    private void getDefaultOptions(Model model){
        model.addAttribute(NamesHelper.ETNIAS_OPTIONS, etniasList);
        model.addAttribute(NamesHelper.SEXO_OPTIONS, sexoList);
        model.addAttribute(NamesHelper.ESCOLAS_OPTIONS, escolaList);

        model.addAttribute(NamesHelper.ETNIAS_SELECIONADA, etniaSelecionado);
        model.addAttribute(NamesHelper.SEXO_SELECIONADA, sexoSelecionado);
        model.addAttribute(NamesHelper.ESCOLAS_SELECIONADA, escolaSelecionado);
    }

    @GetMapping("/")
    private String getAllAlunos(Model model){
        selectedPage = "etnia";
        setOptionsFilter();
        getDefaultOptions(model);
        List<Alunos> alunosList = (filterAlunos != null && !filterAlunos.isEmpty())
                ? filterAlunos : service.getAllAlunos();
        List<String> etniaList = alunosList.stream()
                .map(Alunos::getEtnia).distinct().collect(Collectors.toList());

        List<Integer> quantidadePorEtnia = new ArrayList<>();
        for (String etnia : etniaList) {
            quantidadePorEtnia.add(alunosList.stream().filter(alunos -> alunos.getEtnia().equalsIgnoreCase(etnia)).collect(Collectors.toList()).size());
        }

        System.out.println(etniaList);
        System.out.println(quantidadePorEtnia);

        model.addAttribute(NamesHelper.ABOUT_GRAPH, "Etnia dos alunos");
        model.addAttribute(NamesHelper.DETAIL_GRAPH, "Quantidade de alunos por etnia");
        model.addAttribute(NamesHelper.COLUMM_PARAM, etniaList);
        model.addAttribute(NamesHelper.COLUMNS_DECRIPTION, "Etnias");
        model.addAttribute(NamesHelper.AMOUNT_PARAM, quantidadePorEtnia);

        model.addAttribute("not-found", notFoundAlunos);
        notFoundAlunos = false;
        return "graficos";
    }

    @GetMapping("/sobre")
    private String getSobre(Model model){
        return "sobre";
    }

    @GetMapping("/sexo")
    private String filterSexo(Model model){
        selectedPage = "sexo";

        getDefaultOptions(model);
        List<Alunos> alunosList = (filterAlunos != null && !filterAlunos.isEmpty())
                ? filterAlunos : service.getAllAlunos();

        List<String> sexoList = alunosList.stream()
                .map(Alunos::getSexo).distinct().collect(Collectors.toList());
        List<Integer> quantidadePorSexo = new ArrayList<>();
        for (String sexo : sexoList) {
            quantidadePorSexo.add(alunosList.stream().filter(alunos -> alunos.getSexo().equalsIgnoreCase(sexo)).collect(Collectors.toList()).size());
        }

        System.out.println(sexoList);
        System.out.println(quantidadePorSexo);

        model.addAttribute(NamesHelper.ABOUT_GRAPH, "Sexo dos alunos");
        model.addAttribute(NamesHelper.DETAIL_GRAPH, "Quantidade de alunos por sexo");
        model.addAttribute(NamesHelper.COLUMM_PARAM, sexoList);
        model.addAttribute(NamesHelper.COLUMNS_DECRIPTION, "Sexo");
        model.addAttribute(NamesHelper.AMOUNT_PARAM, quantidadePorSexo);
        return "graficos";
    }

    @GetMapping("/escola")
    private String filterEscola(Model model){
        selectedPage = "escola";

        getDefaultOptions(model);
        List<Alunos> alunosList = (filterAlunos != null && !filterAlunos.isEmpty())
                ? filterAlunos : service.getAllAlunos();
        List<String> escolaList = alunosList.stream()
                .map(Alunos::getEscola_origem).distinct().collect(Collectors.toList());

        List<Integer> quantidadePorEscola = new ArrayList<>();
        for (String escola : escolaList) {
            quantidadePorEscola.add(alunosList.stream().filter(alunos -> alunos.getEscola_origem().equalsIgnoreCase(escola)).collect(Collectors.toList()).size());
        }

        System.out.println(escolaList);
        System.out.println(quantidadePorEscola);

        model.addAttribute(NamesHelper.ABOUT_GRAPH, "Tipo da escola que os alunos estudaram");
        model.addAttribute(NamesHelper.DETAIL_GRAPH, "Quantide de alunos por cada tipo de escola que estudaram anteriormente");
        model.addAttribute(NamesHelper.COLUMM_PARAM, escolaList);
        model.addAttribute(NamesHelper.COLUMNS_DECRIPTION, "Tipo de escola que estudaram");
        model.addAttribute(NamesHelper.AMOUNT_PARAM, quantidadePorEscola);
        return "graficos";
    }

    @PostMapping("/filtrar")
    private String filterByParam(@RequestParam("sexo-op") String sexo,
                                 @RequestParam("etnia-op") String etnia,
                                     @RequestParam("escola-op") String escola){
        List<Alunos> filtro = null;
        sexoSelecionado = sexo;
        etniaSelecionado = etnia;
        escolaSelecionado = escola;
        try {
            filtro = service.getAllAlunos();
            if(sexo != null && !sexo.isEmpty()){
                filtro = filtro.stream()
                        .filter(x -> x.getSexo().equalsIgnoreCase(sexo))
                        .collect(Collectors.toList());
                subTitle = " SEXO ";
            }

            if(etnia != null && !etnia.isEmpty()){
                filtro = filtro.stream()
                        .filter(x -> x.getEtnia().equalsIgnoreCase(etnia))
                        .collect(Collectors.toList());
            }

            if(escola != null && !escola.isEmpty()){
                filtro = filtro.stream()
                        .filter(x -> x.getEscola_origem().equalsIgnoreCase(escola))
                        .collect(Collectors.toList());
            }
        }catch (Exception ex){
            System.out.println(ex.getMessage());
        }
        filterAlunos = (!filtro.isEmpty()) ? filtro : null;
        notFoundAlunos = (filterAlunos != null && filterAlunos.size() > 0);
        switch (selectedPage) {
            case "etnia":
                return "redirect:/";
            case "sexo":
                return "redirect:/sexo";
            case "escola":
                return "redirect:/escola";
        }
        return "graficos";
    }

    @GetMapping("/reset-filter")
    public String resetFilter(){
        selectedPage = "";
        sexoSelecionado = "";
        etniaSelecionado = "";
        escolaSelecionado = "";

        filterAlunos = null;
        return "redirect:/";
    }
}