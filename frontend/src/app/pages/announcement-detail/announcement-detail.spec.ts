import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AnnouncementDetail } from './announcement-detail';

describe('AnnouncementDetail', () => {
  let component: AnnouncementDetail;
  let fixture: ComponentFixture<AnnouncementDetail>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AnnouncementDetail]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AnnouncementDetail);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
