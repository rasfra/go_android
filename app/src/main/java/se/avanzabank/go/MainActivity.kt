package se.avanzabank.go

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.button9)
                .setOnClickListener({
                    startActivity(Intent(this, GoActivity::class.java).putExtra("size", 9))
                })
        findViewById<Button>(R.id.button13)
                .setOnClickListener({
                    startActivity(Intent(this, GoActivity::class.java).putExtra("size", 13))
                })
        findViewById<Button>(R.id.button19)
                .setOnClickListener({
                    startActivity(Intent(this, GoActivity::class.java).putExtra("size", 19))
                })
    }

}
