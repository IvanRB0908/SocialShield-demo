export interface ApiCourse {
  id: number;
  title: string;
  description: string;
  duration: number;
  available: boolean;
  level: string;
  lessons: number;
  tag: string;
  icon: string;
}

export interface CreateCourseRequest {
  title: string;
  duration: number;
  available: boolean;
  icon: string;
  tag: string;
  description: string;
  level: string;
}