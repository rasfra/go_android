package se.avanzabank.go

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import se.avanzabank.go.databinding.ActivityGoBinding
import se.avanzabank.go.game.Board
import se.avanzabank.go.game.Game

class GoActivity : AppCompatActivity() {
    lateinit var game: Game
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityGoBinding = DataBindingUtil.setContentView<ActivityGoBinding>(this, R.layout.activity_go)
        val size = intent.getIntExtra("size", 19)
        game = Game(Board(size))
        val vm = GoViewModel(game)
        binding.vm = vm
        game.addListener(vm)
        game.addListener(binding.goBoard)
    }

    override fun onSaveInstanceState(outState: Bundle?, outPersistentState: PersistableBundle?) {
        super.onSaveInstanceState(outState, outPersistentState)
    }
}