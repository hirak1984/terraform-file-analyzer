<b>What's terraform-file-analyzer?</b>
</br>
It's a CLI to analyze TFstate and plan files to extract the list of actions on resources.
</br>
Actions can be "create", "no-ops" and "delete".
</br>
Examples: 
</br>
If you create two EC2 instances your initial summary.json will look like like the below:
</br>
{"changes":[{"action":"create","address":"aws_instance.blue","id":"i-0f8629b0d2024429e","type":"aws_instance"},{"action":"create","address":"aws_instance.green","id":"i-098326d9e5dc0328e","type":"aws_instance"}]}
</br>
If you delete one of the two EC2 instances your summary.json will look like like the below:
</br>
{"changes":[{"action":"delete","address":"aws_instance.blue","id":"i-0f8629b0d2024429e","type":"aws_instance"},{"action":"no-op","address":"aws_instance.green","id":"i-098326d9e5dc0328e","type":"aws_instance"}]}
</br>
</br>
<b>Pre-Reqs</b>
</br>
terraform binary should be in same repo. TF state/plan files should be passed to this CLI.
</br>
</br>
<b>How To use it?</b>
</br>
a. You need to generate a TF plan file first, e.g.:
</br>
   terraform plan -out plan_example.bin
</br>
b. Then you call this utility:
</br>
   java -jar terraformfileanalyzer-1.0.3.jar plan_example.bin summary.json
</br>
c. Then you run TF apply with the given plan file
</br>
   terraform apply plan_example.bin
</br>
d. Then you can complete the analysis by calling again this utility to match the actual IDs with teh planned changes
</br>
   java -jar terraformfileanalyzer-1.0.3.jar terraform.tfstate summary.json
</br>
</br>
<b>TODO</b>
</br>
Not all drift can be fixed by updating a resource, sometimes resources need to be recreated. 