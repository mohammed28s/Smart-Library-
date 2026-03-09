import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { AuthService } from './auth.service';

describe('AuthService', () => {
  let service: AuthService;

  beforeEach(() => {
    localStorage.clear();
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule]
    });
    service = TestBed.inject(AuthService);
  });

  afterEach(() => {
    localStorage.clear();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should start guest mode and clear auth token', () => {
    localStorage.setItem('smartlibrary_token', 'token123');
    service.continueAsGuest();

    expect(localStorage.getItem('smartlibrary_token')).toBeNull();
    expect(localStorage.getItem('smartlibrary_role')).toBe('GUEST');
    expect(service.isGuest).toBeTrue();
    expect(service.isSessionActive).toBeTrue();
  });

  it('should logout and clear session state', () => {
    localStorage.setItem('smartlibrary_token', 'token123');
    localStorage.setItem('smartlibrary_role', 'USER');
    localStorage.setItem('smartlibrary_username', 'user1');

    service.logout();

    expect(service.token).toBe('');
    expect(service.isAuthenticated).toBeFalse();
    expect(service.isSessionActive).toBeFalse();
    expect(localStorage.getItem('smartlibrary_token')).toBeNull();
    expect(localStorage.getItem('smartlibrary_role')).toBeNull();
    expect(localStorage.getItem('smartlibrary_username')).toBeNull();
  });
});
