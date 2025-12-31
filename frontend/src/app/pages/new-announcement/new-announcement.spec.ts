import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NewAnnouncementComponent } from './new-announcement';

describe('NewAnnouncement', () => {
  let component: NewAnnouncementComponent;
  let fixture: ComponentFixture<NewAnnouncementComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NewAnnouncementComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(NewAnnouncementComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
