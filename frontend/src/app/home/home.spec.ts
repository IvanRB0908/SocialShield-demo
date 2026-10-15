import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { of } from 'rxjs';
import { Home } from './home';
import { CourseApiService } from '../services/course-api.service';

describe('Home', () => {
  let component: Home;
  let fixture: ComponentFixture<Home>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Home],
      providers: [
        provideRouter([]),
        {
          provide: CourseApiService,
          useValue: {
            getCourses: () => of([]),
            getHomePage: () => of({ platformName: 'SocialShield', stats: [], sections: [] }),
            createCourse: () => of({
              id: 1,
              title: 'Test',
              description: '',
              duration: 1,
              available: true,
              level: 'Principiante',
              lessons: 1,
              tag: 'Nuevo',
              icon: '+',
            }),
          },
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(Home);
    component = fixture.componentInstance;
    fixture.detectChanges();
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
