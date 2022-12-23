package practice.webservice.web;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import practice.webservice.service.PostsService;
import practice.webservice.web.dto.PostsResponseDto;
import practice.webservice.web.dto.PostsSaveRequestDto;
import practice.webservice.web.dto.PostsUpdateRequestDto;

@RequiredArgsConstructor
@RestController
public class PostsApiController {

    private final PostsService postsService;

    @PostMapping("/api/v1/posts")
    public Long save(@RequestBody PostsSaveRequestDto requestDto) {
        return postsService.save(requestDto);
    }

    @GetMapping("/api/v1/posts/{id}")
    public PostsResponseDto findById(@PathVariable Long id) {
        return postsService.findById(id);
    }

    @PutMapping("/api/v1/posts/{id}")
    public Long update(@PathVariable Long id, @RequestBody PostsUpdateRequestDto requestDto) {
        return postsService.update(id, requestDto);
    }
}
