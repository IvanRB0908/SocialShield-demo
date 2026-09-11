import { Component } from '@angular/core';
import { Courses } from '../courses/courses';
import { Footer } from '../footer/footer';
import { Header } from '../header/header';
import { Hero } from '../hero/hero';
import { Why } from '../why/why';

@Component({
  imports: [Header, Hero, Courses, Why, Footer],
  selector: 'app-home',
  styleUrl: './home.css',
  templateUrl: './home.html',
})
export class Home {}
