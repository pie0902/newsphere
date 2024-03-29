package news.newsphere.service.user;


import lombok.RequiredArgsConstructor;
import news.newsphere.dto.user.UserResponse;
import news.newsphere.dto.user.UserSigninRequest;
import news.newsphere.dto.user.UserSignupRequest;
import news.newsphere.entity.user.User;
import news.newsphere.repository.user.UserRepository;
import news.newsphere.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    @Autowired
    private final PasswordEncoder passwordEncoder;
    public UserResponse signUp(UserSignupRequest userSignupRequest) {
        boolean userExists = userRepository.findByEmail(userSignupRequest.getEmail()).isPresent();
        if (userExists) {
            throw new IllegalArgumentException("이미 가입된 회원입니다.");
        }
        //비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(userSignupRequest.getPassword());
        //유저 객체 생성
        User newUser = new User(userSignupRequest,encodedPassword);
        //유저 저장
        User savedUser = userRepository.save(newUser); // 생성된 사용자 객체 저장
        //컨트롤러에 데이터 리턴
        UserResponse userResponse = new UserResponse(savedUser);
        return userResponse;
    }

    public UserResponse signin(UserSigninRequest userSigninRequest) {
        //유저 이메일 검증
        User loginUser = userRepository.findByEmail(userSigninRequest.getEmail())
            .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을수 없습니다."));
        //비밀번호 검증
        boolean matches = passwordEncoder.matches(userSigninRequest.getPassword(), loginUser.getPassword());
        if (!matches) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
        // JWT 토큰 생성
        String token = jwtUtil.createToken(loginUser.getEmail(),loginUser.getUserRoleEnum());
        // UserResponse 생성 및 반환
        UserResponse userResponse = new UserResponse(loginUser, token);
        return userResponse;
    }
}
