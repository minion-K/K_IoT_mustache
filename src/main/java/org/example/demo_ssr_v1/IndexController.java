package org.example.demo_ssr_v1;

import lombok.RequiredArgsConstructor;
import org.example.demo_ssr_v1.board.Board;
import org.example.demo_ssr_v1.board.BoardPersistRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class IndexController {
    private final BoardPersistRepository boardPersistRepository;

    @GetMapping("/")
    // http://localhost:8080
    public String index(Model model) {
        List<Board> boardList = boardPersistRepository.findAll();
        model.addAttribute("boardList", boardList);

        return "/index";
    }

    @GetMapping("/board/search")
    public String search(@RequestParam String keyword, Model model) {
        List<Board> searchBoardList = boardPersistRepository.search(keyword);
        model.addAttribute("boardList", searchBoardList);
        System.out.println(searchBoardList);

        return "index";
    }
}
