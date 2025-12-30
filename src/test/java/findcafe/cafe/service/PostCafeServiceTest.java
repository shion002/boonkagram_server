package findcafe.cafe.service;

import findcafe.cafe.dto.postcafedto.PostCafeRequestDto;
import findcafe.cafe.dto.postcafedto.PostCafeResponseDto;
import findcafe.cafe.dto.reviewdto.ReviewResponseDto;
import findcafe.cafe.entity.PostCafeMenu;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.parameters.P;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Slf4j
class PostCafeServiceTest {

    @Autowired private PostCafeService postCafeService;

    @Test
    @Transactional
    void getPost() {
    }

    @Test
    void basicSetting(){
        postCafeService.updatePostBasic();
    }

    @Test
    void updatePostData() {
        List<PostCafeMenu> menus = new ArrayList<>();
        PostCafeMenu postCafeMenu1 = new PostCafeMenu("아메리카노", 2000);
        PostCafeMenu postCafeMenu2 = new PostCafeMenu("카페라떼", 3000);
        PostCafeMenu postCafeMenu3 = new PostCafeMenu("카푸치노", 3500);
        menus.add(postCafeMenu1);
        menus.add(postCafeMenu2);
        menus.add(postCafeMenu3);
        PostCafeRequestDto postCafeRequestDto = new PostCafeRequestDto(1L, "수정 여기는 감성 카페 입니다",
                "수정 01033333333", "수정 인스타그램 주소", "수정 www.웹 주소.ccc", "수정 수정 여기는 감성카페 이고 사진도 잘찍힙니다",
                menus, 33.151621, 108.13516);

        postCafeService.updatePost(postCafeRequestDto, 1L);
    }

    @Test
    void createPostData() {
        List<PostCafeMenu> menus = new ArrayList<>();
        PostCafeMenu postCafeMenu1 = new PostCafeMenu("아메리카노", 2000);
        PostCafeMenu postCafeMenu2 = new PostCafeMenu("카페라떼", 3000);
        PostCafeMenu postCafeMenu3 = new PostCafeMenu("카푸치노", 3500);
        menus.add(postCafeMenu1);
        menus.add(postCafeMenu2);
        menus.add(postCafeMenu3);
        PostCafeRequestDto postCafeRequestDto = new PostCafeRequestDto("카페이름", "주소", "여기는 감성 카페 입니다",
                "01033333333", "인스타그램 주소", "www.웹 주소.ccc", "여기는 감성카페 이고 사진도 잘찍힙니다",
                menus, 35.1321651, 135.13121);
        postCafeService.createPost(postCafeRequestDto);
    }

    @Test
    void deletePostData() {
        postCafeService.deletePost(25302L);
    }
}



















