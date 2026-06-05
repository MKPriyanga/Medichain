import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { DepartmentService } from '../../../core/services/department.service';
import { Department } from '../../../core/models/models';

@Component({
  selector: 'app-department-list',
  imports: [CommonModule, FormsModule],
  templateUrl: './department-list.html'
})
export class DepartmentListComponent implements OnInit {
  private svc = inject(DepartmentService);
  departments: Department[] = [];
  loading = false; showForm = false; saving = false; message = ''; error = '';
  editId: number | null = null;
  form: Department = { name: '', headId: undefined, contactInfo: '', status: 'ACTIVE' };

  ngOnInit() { this.load(); }

  load() {
    this.loading = true;
    this.svc.getAll().subscribe({
      next: d => { this.departments = d; this.loading = false; },
      error: () => { this.error = 'Failed to load.'; this.loading = false; }
    });
  }

  save() {
    this.saving = true;
    const call = this.editId
      ? this.svc.update(this.editId, this.form)
      : this.svc.create(this.form);
    call.subscribe({
      next: () => {
        this.message = this.editId ? 'Department updated.' : 'Department created.';
        this.showForm = false; this.editId = null;
        this.form = { name: '', headId: undefined, contactInfo: '', status: 'ACTIVE' };
        this.load(); this.saving = false;
      },
      error: () => { this.error = 'Failed to save.'; this.saving = false; }
    });
  }

  edit(d: Department) {
    this.editId = d.departmentId!;
    this.form = { ...d };
    this.showForm = true;
  }

  cancel() {
    this.showForm = false; this.editId = null;
    this.form = { name: '', headId: undefined, contactInfo: '', status: 'ACTIVE' };
  }
}
