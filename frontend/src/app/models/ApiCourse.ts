export interface ApiCourse {
  id: string;
  title: string;
  description: string;
  duration: string;
  level: string;
  lessons: number;
  tag: string;
  icon: string;
}

export interface CreateCourseRequest {
  title: string;
  duration: string;
  lessons: number;
  isAvailable: boolean;
}